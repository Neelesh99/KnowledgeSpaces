package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.GPTUserApp
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeFileStore
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeSpaceStore
import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.config.Dependencies
import com.neelesh.storage.BlobStore
import com.neelesh.storage.InMemoryBlobStore
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
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(stubLlmApp.server(), blobStore, inMemoryKnowledgeFileStore, inMemoryKnowledgeSpaceStore)
        )
        return server.asServer(Undertow(port = port)).start()
    }
}