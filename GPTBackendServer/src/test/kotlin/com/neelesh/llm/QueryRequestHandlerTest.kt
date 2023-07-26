package com.neelesh.llm

import arrow.core.left
import arrow.core.right
import com.neelesh.model.KnowledgeFile
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleQueryRequest
import com.neelesh.storage.BlobStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.http4k.core.*
import org.http4k.format.Jackson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QueryRequestHandlerTest {

    val knowledgeFileStore = mockk<KnowledgeFileStore>()
    val llmClient = mockk<HttpHandler>()

    val handler = QueryRequestHandler(knowledgeFileStore, llmClient)

    @Test
    fun `will return exception if user does not have this knowledge file id`() {
        val queryRequestDto = SimpleQueryRequest("test@domain.com", "someFileId", "someQuery")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns Exception("This user does not have a file named someFileName").left()
        val response = handler.handle(queryRequestDto)
        response.fold(
            {
                assertEquals("This user does not have a file named someFileName", it.message!!)
            },
            {
                Assertions.fail("Expected an exception")
            })
    }

    @Test
    fun `will return exception if llm query does not work`() {
        val queryRequestDto = SimpleQueryRequest("test@domain.com", "someFileId", "someQuery")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns KnowledgeFile(
            "someFileId",
            "test@domain.com",
            "someFileName",
            listOf("someBlobId"),
            "{}"
        ).right()

        every { llmClient.invoke(any()) } returns Response(Status.INTERNAL_SERVER_ERROR)

        val response = handler.handle(queryRequestDto)
        response.fold(
            {
                assertEquals("Error from LLM API code: 500", it.message!!)
            },
            {
                Assertions.fail("Expected an exception")
            })
    }

    @Test
    fun `will return response if all parts work`() {
        val queryRequestDto = SimpleQueryRequest("test@domain.com", "someFileId", "someQuery")
        val knowledgeFile = KnowledgeFile(
            "someFileId",
            "test@domain.com",
            "someFileName",
            listOf("someBlobId"),
            "{}"
        )
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns knowledgeFile.right()

        val requestSlot = slot<Request>()

        every { llmClient.invoke(capture(requestSlot)) } returns Response(Status.OK).body("hello")

        val response = handler.handle(queryRequestDto)
        response.fold(
            {
                Assertions.fail(it.message)
            },
            {
                assertEquals("hello", it)
            })
        val capturedRequest = requestSlot.captured
        val formBody = MultipartFormBody.from(capturedRequest)
        assertEquals("someQuery", formBody.field("query")!!.value)
        val knowledgeFileString = String(formBody.file("knowledgeFile.json")!!.content.readAllBytes())
        assertEquals(knowledgeFile, KnowledgeFile.fromJson(Jackson.parse(knowledgeFileString)))
    }

}