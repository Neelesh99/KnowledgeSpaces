package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.GPTUserApp
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeFileStore
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeSpaceStore
import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.config.Dependencies
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.storage.BlobStore
import com.neelesh.storage.InMemoryBlobStore
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.MultipartFormBody
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class DownloadBlobAcceptanceTest {
    @field:TempDir
    lateinit var testingDirectory: File

    val blobStore: BlobStore by lazy {
        InMemoryBlobStore(testingDirectory)
    }

    private val inMemoryKnowledgeFileStore = InMemoryKnowledgeFileStore(emptyList())

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        val expectedReference = BlobReference(
            "someBlobId",
            DataType.PLAIN_TEXT,
            "someFileName"
        )
        val blobData = "someData".byteInputStream()
        blobStore.storeBlob(expectedReference, blobData)
        val body = "{\"blobId\":\"someBlobId\"}"
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/download/blob?api=42")
            .body(body)
        val testClient = OkHttp()
        val response = testClient(request)
        val received = MultipartFormBody.from(response)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someData", String(received.file("someFileName")!!.content.readAllBytes()))
    }

    fun setupClient(stubLlmApp: StubLLMApp, port: Int): Http4kServer {
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(stubLlmApp.server(), blobStore, inMemoryKnowledgeFileStore, InMemoryKnowledgeSpaceStore())
        )
        return server.asServer(Undertow(port = port)).start()
    }
}