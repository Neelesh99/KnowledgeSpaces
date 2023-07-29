package com.neelesh.acceptance.LLMAcceptanceTests

import org.http4k.core.*
import org.http4k.core.Request
import org.junit.jupiter.api.fail

import com.neelesh.GPTUserApp
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeFileStore
import com.neelesh.acceptance.Stubs.InMemoryKnowledgeSpaceStore
import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.config.Dependencies
import com.neelesh.model.*
import com.neelesh.storage.BlobStore
import com.neelesh.storage.InMemoryBlobStore
import org.http4k.client.OkHttp
import org.http4k.lens.MultipartFormFile
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class UploadBlobAcceptanceTest {

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
        val knowledgeFile = KnowledgeFile(
            "someKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        inMemoryKnowledgeFileStore.saveKnowledgeFile(knowledgeFile)
        val inputForm = MultipartFormBody().plus("dataType" to "PLAIN_TEXT")
            .plus("fileName" to "someFileName")
            .plus("knowledgeFileTarget" to "someKnowledgeFileId")
            .plus("email" to "someEmail")
            .plus("file" to MultipartFormFile(
                "someFileName",
                ContentType.OCTET_STREAM,
                "someText".byteInputStream()
            ))
        val request = Request(Method.POST, "http://localhost:${server.port()}/a/upload/blob?api=42")
            .body(inputForm)
            .header("Content-Type", ContentType.MultipartFormWithBoundary(inputForm.boundary).toHeaderValue())

        val testClient = OkHttp()
        val response = testClient(request)
        assertEquals(Status.OK, response.status)
        assertEquals(response.bodyString(), "someKnowledgeFileId")
        assertEquals("someKnowledgeFileId", response.bodyString())
        val updatedKnowledgeFile = inMemoryKnowledgeFileStore.fileStore[0]
        val blobId = updatedKnowledgeFile.blobIds[1]
        val expectedReference = BlobReference(
            blobId,
            DataType.PLAIN_TEXT,
            "someFileName"
        )
        blobStore.getBlob(blobId).fold(
            {
                fail(it.message)
            }, {
                assertEquals(expectedReference, it.first)
                assertEquals("someText", String(it.second.readAllBytes()))
            }
        )
    }

    fun setupClient(stubLlmApp: StubLLMApp, port: Int): Http4kServer {
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(stubLlmApp.server(), blobStore, inMemoryKnowledgeFileStore, InMemoryKnowledgeSpaceStore()))
        return server.asServer(Undertow(port = port)).start()
    }

}