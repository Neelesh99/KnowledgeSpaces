package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.KnowledgeSpace
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetKnowledgeSpacesAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        inMemoryKnowledgeSpaceStore.saveKnowledgeSpace(getSomeKnowledgeSpace("someSpaceId"))
        inMemoryKnowledgeSpaceStore.saveKnowledgeSpace(getSomeKnowledgeSpace("someOtherSpaceId"))
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/getSpaces?api=42")
            .body("{\"email\":\"someEmail\"}")
        val testClient = OkHttp()
        val response = testClient(request)
        assertEquals(Status.OK, response.status)
        assertEquals(listOf("someSpaceId", "someOtherSpaceId"), Jackson.parse(response.bodyString()).map { node -> node.get("id").textValue() })
    }

    private fun getSomeKnowledgeSpace(knowledgeSpaceId: String) = KnowledgeSpace(
        knowledgeSpaceId,
        "someName",
        "someEmail",
        listOf("someKnowledgeFileId")
    )

}