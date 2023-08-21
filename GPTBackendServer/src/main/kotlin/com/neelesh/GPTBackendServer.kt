package com.neelesh

import com.neelesh.config.Config
import com.neelesh.config.Dependencies
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import com.neelesh.persistence.MongoBackedKnowledgeFileStore
import com.neelesh.persistence.MongoBackedKnowledgeSpaceStore
import com.neelesh.security.InsecureTokenChecker
import com.neelesh.storage.InMemoryBlobStore
import com.neelesh.user.MongoBasedOAuthPersistence
import com.neelesh.user.User
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.cloudnative.env.Environment
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import java.io.File
import java.time.Clock


fun main() {
    val client1 = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val originalUrl = original.url
            val newUrl = originalUrl.newBuilder()
                .scheme("http")
                .host("localhost")
                .port(2323)
                .build()
            val requestBuilder= original.newBuilder()
                .url(newUrl)
            val request= requestBuilder.build()
            chain.proceed(request)
        }
        .build()
    val client: HttpHandler = OkHttp(client1)
    val config = Config.fromEnvironment(Environment.ENV)
    val mongoClient = KMongo.createClient("mongodb+srv://firehzb:${config.mongoDBPassword}@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority")
    val db = mongoClient.getDatabase("myStuff")
    val userCollection = db.getCollection<User>("user")
    val mongoOAuthPersistence = MongoBasedOAuthPersistence(userCollection, Clock.systemUTC(), InsecureTokenChecker)
    //val storage = StorageOptions.newBuilder().setProjectId(config.googleProjectId).build().getService()
    val dependencies = Dependencies(
        client,
        InMemoryBlobStore(File("/Users/neeleshravichandran/IdeaProjects/customGPTSlackbot/storage")),
        MongoBackedKnowledgeFileStore(db.getCollection<KnowledgeFile>("knowledgeFileCollection")),
        MongoBackedKnowledgeSpaceStore(db.getCollection<KnowledgeSpace>("knowledgeSpaceCollection")),
        mongoOAuthPersistence
    )

    val printingApp: HttpHandler = PrintRequest().then(
            GPTUserApp(mongoOAuthPersistence, dependencies, config))

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
