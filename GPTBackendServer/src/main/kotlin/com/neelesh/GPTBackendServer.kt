package com.neelesh

import com.google.cloud.storage.StorageOptions
import com.neelesh.config.Config
import com.neelesh.config.Dependencies
import com.neelesh.model.BlobReference
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import com.neelesh.persistence.MongoBackedKnowledgeFileStore
import com.neelesh.persistence.MongoBackedKnowledgeSpaceStore
import com.neelesh.security.InsecureTokenChecker
import com.neelesh.storage.GoogleBlobStore
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
import java.time.Clock
import java.time.Duration


fun main() {
    val config = Config.fromEnvironment(Environment.ENV)
    val client1 = OkHttpClient.Builder()
        .connectTimeout(Duration.ofMinutes(5))
        .readTimeout(Duration.ofMinutes(5))
        .callTimeout(Duration.ofMinutes(5))
        .writeTimeout(Duration.ofMinutes(5))
        .addInterceptor { chain ->
            val original = chain.request()
            val originalUrl = original.url
            val newUrl = originalUrl.newBuilder()
                .scheme(config.llmServerScheme)
                .host(config.llmServerHost)
                .port(config.llmServerPort)
                .build()
            val requestBuilder= original.newBuilder()
                .url(newUrl)
            val request= requestBuilder.build()
            chain.proceed(request)
        }
        .build()
    val client: HttpHandler = OkHttp(client1)
    val mongoClient = KMongo.createClient("mongodb+srv://firehzb:${config.mongoDBPassword}@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority")
    val db = mongoClient.getDatabase("myStuff")
    val userCollection = db.getCollection<User>("user")
    val mongoOAuthPersistence = MongoBasedOAuthPersistence(userCollection, Clock.systemUTC(), InsecureTokenChecker)
    //val storage = StorageOptions.newBuilder().setProjectId(config.googleProjectId).build().getService()
    val storage = StorageOptions.getDefaultInstance().getService()
    val blobCollection = db.getCollection<BlobReference>("blobInfo")
    val googleBlobStore = GoogleBlobStore(storage, blobCollection, config.storageBucket)
    val dependencies = Dependencies(
        client,
        googleBlobStore,
        MongoBackedKnowledgeFileStore(db.getCollection<KnowledgeFile>("knowledgeFileCollection")),
        MongoBackedKnowledgeSpaceStore(db.getCollection<KnowledgeSpace>("knowledgeSpaceCollection")),
        mongoOAuthPersistence,
        storage
    )

    val printingApp: HttpHandler = PrintRequest().then(
            GPTUserApp(mongoOAuthPersistence, dependencies, config))

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
