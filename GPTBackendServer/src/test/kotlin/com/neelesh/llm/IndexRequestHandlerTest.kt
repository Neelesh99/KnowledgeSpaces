package com.neelesh.llm

import arrow.core.left
import arrow.core.right
import com.neelesh.model.*
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleIndexRequest
import com.neelesh.storage.BlobStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.http4k.core.*
import org.http4k.core.Request
import org.http4k.format.Jackson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndexRequestHandlerTest {

    val blobStore = mockk<BlobStore>()
    val knowledgeFileStore = mockk<KnowledgeFileStore>()
    val llmClient = mockk<HttpHandler>()

    val indexRequestHandler = IndexRequestHandler(blobStore, knowledgeFileStore, llmClient)

    @Test
    fun `will return exception if user does not have this knowledge file name`() {
        val indexRequestDto = SimpleIndexRequest("test@domain.com", "someFileId")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns Exception("This user does not have a file named someFileName").left()
        val response = indexRequestHandler.handle(indexRequestDto)
        response.fold(
            {
                assertEquals("This user does not have a file named someFileName", it.message!!)
            },
            {
                Assertions.fail("Expected an exception")
            })
    }

    @Test
    fun `will return exception if blob cannot be found`() {
        val indexRequestDto = SimpleIndexRequest("test@domain.com", "someFileId")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns KnowledgeFile("someFileId", "test@domain.com", "someName", listOf("blobId"), "{}").right()
        every {
            blobStore.getBlob("blobId")
        } returns Exception("Blob with Id: blobId cannot be found").left()
        val response = indexRequestHandler.handle(indexRequestDto)
        response.fold(
            {
                assertEquals("Blob with Id: blobId cannot be found", it.message!!)
            },
            {
                Assertions.fail("Expected an exception")
            })
    }

    @Test
    fun `will return exception if llm api call fails`() {
        val indexRequestDto = SimpleIndexRequest("test@domain.com", "someFileId")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns KnowledgeFile("someFileId", "test@domain.com", "someName", listOf("blobId"), "{}").right()
        every {
            blobStore.getBlob("blobId")
        } returns (BlobReference("blobId", DataType.PLAIN_TEXT, "filename.txt") to "someText".byteInputStream()).right()
        every { llmClient(any()) } returns Response(Status.INTERNAL_SERVER_ERROR)
        val response = indexRequestHandler.handle(indexRequestDto)
        response.fold(
            {
                assertEquals("Error from LLM API code: 500", it.message!!)
            },
            {
                Assertions.fail("Expected an exception")
            })
    }

    @Test
    fun `will return runId if handle is successful`() {
        val indexRequestDto = SimpleIndexRequest("test@domain.com", "someFileId")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns KnowledgeFile("someFileId", "test@domain.com", "someName", listOf("blobId"), "{}").right()
        every {
            blobStore.getBlob("blobId")
        } returns (BlobReference("blobId", DataType.PLAIN_TEXT, "someFile.txt") to "someText".byteInputStream()).right()
        val requestSlot = slot<Request>()
        every { llmClient(capture(requestSlot)) } returns Response(Status.OK).body("{\"runId\":\"someRunId\"}")
        val response = indexRequestHandler.handle(indexRequestDto)
        response.fold(
            {
                Assertions.fail(it.message)
            },
            {
                assertEquals("someRunId", it)
            })
        val expectedIndexRequest = IndexRequest(
            UserDetails("test@domain.com"),
            "someFileId",
            listOf(
                BlobReference(
                    "blobId",
                    DataType.PLAIN_TEXT,
                    "someFile.txt"
                )
            )
        )
//        val formBody = requestSlot.captured.body as MultipartFormBody
        val formBody = MultipartFormBody.from(requestSlot.captured)
        val actualIndexRequest = IndexRequest.fromJson(Jackson.parse(String(formBody.file("indexRequest")!!.content.readAllBytes())))
        assertEquals("/api/v1/llm/index", requestSlot.captured.uri.toString())
        assertEquals(expectedIndexRequest, actualIndexRequest)
        assertEquals("someText", String(formBody.file("someFile.txt")!!.content.readAllBytes()))
    }

}