package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.GPTUserApp
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

class IndexRequestsAcceptanceTest {

    @field:TempDir
    lateinit var testingDirectory: File

    val blobStore: BlobStore by lazy {
        InMemoryBlobStore(testingDirectory)
    }

    private val inMemoryKnowledgeFileStore = InMemoryKnowledgeFileStore(emptyList())

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList())
        val server = setupClient(stubLlmApp, 0)
        inMemoryKnowledgeFileStore.saveKnowledgeFile(KnowledgeFile(
            "someKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        ))
        val blobReference = BlobReference("someBlobId",  DataType.PLAIN_TEXT,"someFile.txt")
        blobStore.storeBlob(blobReference, "someText".byteInputStream())
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/sendRequest")
            .body("{\"email\":\"someEmail\",\"knowledgeFileTarget\":\"someKnowledgeFileId\"}")

        val testClient = OkHttp()
        val response = testClient(request)
        val expectedIndexRequest = IndexRequest(
            UserDetails("someEmail"),
            "someKnowledgeFileId",
            listOf(BlobReference("someBlobId", DataType.PLAIN_TEXT, "someFile.txt"))
        )
        val expectedFile = MultipartFormFile("someFile.txt", ContentType.OCTET_STREAM, "someText".byteInputStream())
        assertEquals(Status.OK, response.status)
        assertEquals(expectedIndexRequest, stubLlmApp.savedIndexRequests.get(0).first)
        assertFormFileIsTheSame(expectedFile, stubLlmApp.savedIndexRequests.get(0).second.file("someFile.txt")!!)
    }

    fun assertFormFileIsTheSame(expected: MultipartFormFile, actual: MultipartFormFile) {
        assertEquals(expected.filename, actual.filename)
        assertEquals(expected.contentType, actual.contentType)
        val consumedExpected = String(expected.content.readAllBytes())
        val consumedActual = String(actual.content.readAllBytes())
        assertEquals(consumedExpected, consumedActual)
    }

    fun setupClient(stubLlmApp: StubLLMApp, port: Int): Http4kServer {
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(stubLlmApp.server(), blobStore, inMemoryKnowledgeFileStore))
        return server.asServer(Undertow(port = port)).start()
    }

}