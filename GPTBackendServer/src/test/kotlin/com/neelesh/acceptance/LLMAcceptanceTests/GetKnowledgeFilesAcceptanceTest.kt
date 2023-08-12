package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.KnowledgeFile
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetKnowledgeFilesAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        inMemoryKnowledgeFileStore.saveKnowledgeFile(getSomeKnowledgeFile("someKnowledgeFileId"))
        inMemoryKnowledgeFileStore.saveKnowledgeFile(getSomeKnowledgeFile("someOtherKnowledgeFileId"))
        inMemoryKnowledgeFileStore.saveKnowledgeFile(getSomeKnowledgeFile("someOtherOtherKnowledgeFileId"))
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/getFiles?api=42")
            .body("{\"email\":\"someEmail\"}")
        val testClient = OkHttp()
        val response = testClient(request)
        assertEquals(Status.OK, response.status)
        assertEquals(listOf("someKnowledgeFileId", "someOtherKnowledgeFileId", "someOtherOtherKnowledgeFileId"), Jackson.parse(response.bodyString()).map { node -> node.get("id").textValue() })
    }

    private fun getSomeKnowledgeFile(knowledgeFileId: String) = KnowledgeFile(
        knowledgeFileId,
        "someEmail",
        "someKnowledgeFileName",
        listOf("someBlobId"),
        "{}"
    )

}