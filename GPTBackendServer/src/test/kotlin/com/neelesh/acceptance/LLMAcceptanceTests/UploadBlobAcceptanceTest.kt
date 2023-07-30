package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.model.KnowledgeFile
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class UploadBlobAcceptanceTest : BaseAcceptanceTest() {

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
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/upload/blob?api=42")
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

}