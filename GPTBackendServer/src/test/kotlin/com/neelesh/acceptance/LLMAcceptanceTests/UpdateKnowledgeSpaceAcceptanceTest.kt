package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UpdateKnowledgeSpaceAcceptanceTest : BaseAcceptanceTest() {
    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        val knowledgeSpace = KnowledgeSpace(
            "someSpaceId",
            "someKnowledgeSpaceName",
            "someEmail",
            listOf("someFileId")
        )
        inMemoryKnowledgeSpaceStore.saveKnowledgeSpace(
            knowledgeSpace
        )
        val knowledgeFile = KnowledgeFile(
            "someOtherFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        inMemoryKnowledgeFileStore.saveKnowledgeFile(
            knowledgeFile
        )
        val body = "{\"knowledgeSpaceId\":\"someSpaceId\",\"email\":\"someEmail\",\"newName\":\"newSpaceName\",\"newFiles\":[\"someOtherFileId\"]}"
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/knowledgeSpace/update?api=42")
            .body(body)
        val testClient = OkHttp()
        val response = testClient(request)
        Assertions.assertEquals(Status.OK, response.status)
        val expectedKnowledgeSpace = KnowledgeSpace(
            "someSpaceId",
            "newSpaceName",
            "someEmail",
            listOf("someOtherFileId")
        )
        val receivedKnowledgeSpace = inMemoryKnowledgeSpaceStore.fileStore[0]
        Assertions.assertEquals(expectedKnowledgeSpace, receivedKnowledgeSpace)
    }
}