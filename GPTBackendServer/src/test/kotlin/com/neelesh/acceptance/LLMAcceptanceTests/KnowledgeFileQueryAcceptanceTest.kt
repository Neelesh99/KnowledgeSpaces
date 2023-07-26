package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.GPTUserApp
import com.neelesh.acceptance.LLMAcceptanceTests.IndexRequestsAcceptanceTest.Companion.assertFormFileIsTheSame
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeFileStore
import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.config.Dependencies
import com.neelesh.model.*
import com.neelesh.storage.BlobStore
import com.neelesh.storage.InMemoryBlobStore
import org.http4k.client.OkHttp
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.lens.MultipartFormFile
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class KnowledgeFileQueryAcceptanceTest {

    @field:TempDir
    lateinit var testingDirectory: File

    val blobStore: BlobStore by lazy {
        InMemoryBlobStore(testingDirectory)
    }

    private val inMemoryKnowledgeFileStore = InMemoryKnowledgeFileStore(emptyList())

    @Test
    fun `will receive and send query to llm backend`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        val knowledgeFile = KnowledgeFile(
            "someKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        inMemoryKnowledgeFileStore.saveKnowledgeFile(
            knowledgeFile
        )
        val blobReference = BlobReference("someBlobId",  DataType.PLAIN_TEXT,"someFile.txt")
        blobStore.storeBlob(blobReference, "someText".byteInputStream())
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/queryRequest?api=42")
            .body("{\"email\":\"someEmail\",\"knowledgeFileTarget\":\"someKnowledgeFileId\",\"query\":\"hello\"}")

        val testClient = OkHttp()
        val response = testClient(request)
        assertEquals(Status.OK, response.status)
        assertEquals(knowledgeFile, stubLlmApp.savedQueryRequests.get(0).first)
        assertEquals("hello", stubLlmApp.savedQueryRequests.get(0).second)
        assertEquals("hello", response.bodyString())
    }

    fun setupClient(stubLlmApp: StubLLMApp, port: Int): Http4kServer {
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(stubLlmApp.server(), blobStore, inMemoryKnowledgeFileStore)
        )
        return server.asServer(Undertow(port = port)).start()
    }

}