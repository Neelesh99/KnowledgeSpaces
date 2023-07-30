package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KnowledgeSpaceQueryAcceptanceTest : BaseAcceptanceTest(){

    @Test
    fun `will receive and send query to llm backend`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        val knowledgeFilesForThisSpace = setupKnowledgeFiles()
        val knowledgeFileIds = knowledgeFilesForThisSpace.map { it.id }
        val knowledgeSpace = KnowledgeSpace("someKnowledgeSpaceId", "someName", "someEmail", knowledgeFileIds)
        inMemoryKnowledgeSpaceStore.saveKnowledgeSpace(knowledgeSpace)
        val blobReference = BlobReference("someBlobId",  DataType.PLAIN_TEXT,"someFile.txt")
        blobStore.storeBlob(blobReference, "someText".byteInputStream())
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/space/queryRequest?api=42")
            .body("{\"email\":\"someEmail\",\"knowledgeSpaceTarget\":\"someKnowledgeSpaceId\",\"query\":\"hello\"}")

        val testClient = OkHttp()
        val response = testClient(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals(knowledgeSpace, stubLlmApp.savedSpacesQueryRequests.get(0).first)
        Assertions.assertEquals("hello", stubLlmApp.savedSpacesQueryRequests.get(0).second)
        Assertions.assertEquals("hello", response.bodyString())
    }

    private fun setupKnowledgeFiles(): List<KnowledgeFile> {
        val firstKnowledgeFile = KnowledgeFile(
            "someKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )

        val secondKnowledgeFile = KnowledgeFile(
            "someOtherKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        inMemoryKnowledgeFileStore.saveKnowledgeFile(
            firstKnowledgeFile
        )
        inMemoryKnowledgeFileStore.saveKnowledgeFile(
            secondKnowledgeFile
        )

        return listOf(firstKnowledgeFile, secondKnowledgeFile)
    }

}