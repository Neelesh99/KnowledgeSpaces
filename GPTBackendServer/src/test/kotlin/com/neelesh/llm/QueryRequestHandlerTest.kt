package com.neelesh.llm

import arrow.core.left
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleQueryRequest
import com.neelesh.storage.BlobStore
import io.mockk.every
import io.mockk.mockk
import io.undertow.server.HttpHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class QueryRequestHandlerTest {

    val blobStore = mockk<BlobStore>()
    val knowledgeFileStore = mockk<KnowledgeFileStore>()
    val llmClient = mockk<HttpHandler>()

    val handler = QueryRequestHandler(blobStore, knowledgeFileStore, llmClient)

    @Test
    fun `will return exception if user does not have this knowledge file id`() {
        val queryRequestDto = SimpleQueryRequest("test@domain.com", "someFileId", "someQuery")
        every {
            knowledgeFileStore.getKnowledgeFile("someFileId", "test@domain.com")
        } returns Exception("This user does not have a file named someFileName").left()
        val response = handler.handle(queryRequestDto)
        response.fold(
            {
                Assertions.assertEquals("This user does not have a file named someFileName", it.message!!)
            },
            {
                Assertions.fail("Expected an exception")
            })
    }

}