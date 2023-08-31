package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.KnowledgeFile
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DeleteNewKnowledgeFileAcceptanceTest : BaseAcceptanceTest(){

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        val knowledgeFile = KnowledgeFile(
            "someFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        inMemoryKnowledgeFileStore.saveKnowledgeFile(
            knowledgeFile
        )
        val body = "{\"knowledgeFileId\":\"someFileId\",\"email\":\"someEmail\"}"
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/knowledgeFile/delete?api=42")
            .body(body)
        val testClient = OkHttp()
        val response = testClient(request)
        Assertions.assertEquals(Status.OK, response.status)
        val responseString = response.bodyString()
        val receivedKnowledgeFile = inMemoryKnowledgeFileStore.fileStore
        Assertions.assertEquals("true", responseString)
        Assertions.assertEquals(0, receivedKnowledgeFile.size)
    }
}