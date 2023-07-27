package com.neelesh.llm

import arrow.core.left
import arrow.core.right
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.persistence.KnowledgeSpaceStore
import com.neelesh.routes.SimpleSpaceQueryRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.http4k.core.*
import org.http4k.format.Jackson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SpaceQueryRequestHandlerTest {

    val knowledgeFileStore = mockk<KnowledgeFileStore>()
    val knowledgeSpaceStore = mockk<KnowledgeSpaceStore>()
    val llmClient = mockk<HttpHandler>()

    val handler = SpacesQueryRequestHandler(knowledgeFileStore, knowledgeSpaceStore, llmClient)

    @Test
    fun `will return exception if user does not have this knowledge space id`() {
        val queryRequestDto = SimpleSpaceQueryRequest("test@domain.com", "someFileId", "someQuery")
        every {
            knowledgeSpaceStore.getKnowledgeSpace("someFileId", "test@domain.com")
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
    fun `will return exception if user does not have this knowledge file id`() {
        val queryRequestDto = SimpleSpaceQueryRequest("test@domain.com", "someFileId", "someQuery")
        every {
            knowledgeSpaceStore.getKnowledgeSpace("someFileId", "test@domain.com")
        } returns KnowledgeSpace(
            "someSpaceId",
            "someSpaceName",
            "test@domain.com",
            listOf("someFileId")
        ).right()

        every { knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
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
        val queryRequestDto = SimpleSpaceQueryRequest("test@domain.com", "someFileId", "someQuery")
        every {
            knowledgeSpaceStore.getKnowledgeSpace("someFileId", "test@domain.com")
        } returns KnowledgeSpace(
            "someSpaceId",
            "someSpaceName",
            "test@domain.com",
            listOf("someFileId")
        ).right()
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
        val queryRequestDto = SimpleSpaceQueryRequest("test@domain.com", "someFileId", "someQuery")
        val knowledgeSpace = KnowledgeSpace(
            "someSpaceId",
            "someSpaceName",
            "test@domain.com",
            listOf("someFileId")
        )
        every {
            knowledgeSpaceStore.getKnowledgeSpace("someFileId", "test@domain.com")
        } returns knowledgeSpace.right()
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns KnowledgeFile(
            "someFileId",
            "test@domain.com",
            "someFileName",
            listOf("someBlobId"),
            "{}"
        ).right()

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
        val knowledgeSpaceString = String(formBody.file("knowledgeSpace.json")!!.content.readAllBytes())
        assertEquals(knowledgeSpace, KnowledgeSpace.fromJson(Jackson.parse(knowledgeSpaceString)))
    }

}