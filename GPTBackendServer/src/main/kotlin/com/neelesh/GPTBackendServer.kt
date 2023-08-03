package com.neelesh

import com.neelesh.config.Dependencies
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import com.neelesh.persistence.MongoBackedKnowledgeFileStore
import com.neelesh.persistence.MongoBackedKnowledgeSpaceStore
import com.neelesh.storage.InMemoryBlobStore
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import java.io.File


fun main() {
    val client: HttpHandler = OkHttp()
    val mongoClient = KMongo.createClient("mongodb+srv://firehzb:$mongodbPass@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority")
    val db = mongoClient.getDatabase("myStuff")
    val dependencies = Dependencies(
        client,
        InMemoryBlobStore(File("/storage")),
        MongoBackedKnowledgeFileStore(db.getCollection<KnowledgeFile>("knowledgeFileCollection")),
        MongoBackedKnowledgeSpaceStore(db.getCollection<KnowledgeSpace>("knowledgeSpaceCollection"))
    )
    val corsPolicy = CorsPolicy(
        originPolicy = OriginPolicy.AllowAll(), // TODO Replace with the appropriate client origin(s)
        headers = emptyList(), // listOf("Content-Type", "Authorization"), // TODO Consider adding back Authorization
        methods = listOf(Method.GET, Method.POST /*, Method.PUT, Method.DELETE */) // TODO Double Check Completeness
    )

    val corsMiddleware = ServerFilters.Cors(corsPolicy)
    val printingApp: HttpHandler = PrintRequest().then(
            GPTUserApp(mongoOAuthPersistence, dependencies))

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
