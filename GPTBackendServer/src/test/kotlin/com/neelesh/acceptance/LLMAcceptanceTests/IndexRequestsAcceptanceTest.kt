package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.*
import org.http4k.client.OkHttp
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndexRequestsAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
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
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/sendRequest?api=42")
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
        assertEquals("someRunId", response.bodyString())
    }

    companion object {
        fun assertFormFileIsTheSame(expected: MultipartFormFile, actual: MultipartFormFile) {
            assertEquals(expected.filename, actual.filename)
            assertEquals(expected.contentType.value, actual.contentType.value)
            val consumedExpected = String(expected.content.readAllBytes())
            val consumedActual = String(actual.content.readAllBytes())
            assertEquals(consumedExpected, consumedActual)
        }
    }

}