package com.neelesh.routes

import arrow.core.right
import com.neelesh.persistence.KnowledgeFileHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UpdateKnowledgeFileRouteTest {

    val knowledgeFileHandler = mockk<KnowledgeFileHandler>()
    val route = UpdateKnowledgeFileRoute(knowledgeFileHandler)

    @Test
    fun `will download blob as multipart form`() {
        every { knowledgeFileHandler.update(any()) } returns "someFileId".right()

        val request = Request(Method.POST, "/knowledgeFile/create").body("{\"knowledgeFileId\":\"someFileId\",\"email\":\"someEmail\",\"newName\":\"newFileName\",\"newBlobs\":[\"someOtherBlobId\"]}")
        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someFileId", response.bodyString())
    }

}