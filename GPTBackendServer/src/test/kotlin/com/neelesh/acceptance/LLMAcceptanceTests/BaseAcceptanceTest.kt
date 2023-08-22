package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.GPTUserApp
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeFileStore
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeSpaceStore
import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.config.Config
import com.neelesh.config.Dependencies
import com.neelesh.storage.BlobStore
import com.neelesh.storage.InMemoryBlobStore
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.io.TempDir
import java.io.File


open class BaseAcceptanceTest {

    @field:TempDir
    protected lateinit var testingDirectory: File

    protected val blobStore: BlobStore by lazy {
        InMemoryBlobStore(testingDirectory)
    }

    protected val inMemoryKnowledgeFileStore = InMemoryKnowledgeFileStore(emptyList())
    protected val inMemoryKnowledgeSpaceStore = InMemoryKnowledgeSpaceStore()

    fun setupClient(stubLlmApp: StubLLMApp, port: Int): Http4kServer {
        val llmServer = stubLlmApp.server().asServer(Undertow(port = 0)).start()
        val client1 = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url
                val newUrl = originalUrl.newBuilder()
                    .scheme("http")
                    .host("localhost")
                    .port(llmServer.port())
                    .build()
                val requestBuilder= original.newBuilder()
                    .url(newUrl)
                val request= requestBuilder.build()
                chain.proceed(request)
            }
            .build()
        val client: HttpHandler = OkHttp(client1)
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(
                client,
                blobStore,
                inMemoryKnowledgeFileStore,
                inMemoryKnowledgeSpaceStore,
                InsecureCookieBasedOAuthPersistence("someCookie")
            ),
            Config.DEFAULT
        )
        return server.asServer(Undertow(port = port)).start()
    }
}