package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.model.KnowledgeFile
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KnowledgeFileQueryAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `will receive and send query to llm backend`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
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

}