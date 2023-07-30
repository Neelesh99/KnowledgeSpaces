package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.model.KnowledgeFile
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UpdateNewKnowledgeFileAcceptanceTest : BaseAcceptanceTest(){

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
        val blobReference = BlobReference("someBlobId",  DataType.PLAIN_TEXT,"someFile.txt")
        val otherBlobReference = BlobReference("someOtherBlobId",  DataType.PLAIN_TEXT,"someFile.txt")
        blobStore.storeBlob(blobReference, "someText".byteInputStream())
        blobStore.storeBlob(otherBlobReference, "someText".byteInputStream())
        val body = "{\"knowledgeFileId\":\"someFileId\",\"email\":\"someEmail\",\"newName\":\"newFileName\",\"newBlobs\":[\"someOtherBlobId\"]}"
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/knowledgeFile/update?api=42")
            .body(body)
        val testClient = OkHttp()
        val response = testClient(request)
        Assertions.assertEquals(Status.OK, response.status)
        val expectedKnowledgeFile = KnowledgeFile(
            "someFileId",
            "someEmail",
            "newFileName",
            listOf("someOtherBlobId"),
            "{}"
        )
        val receivedKnowledgeFile = inMemoryKnowledgeFileStore.fileStore[0]
        Assertions.assertEquals(expectedKnowledgeFile, receivedKnowledgeFile)
    }
}