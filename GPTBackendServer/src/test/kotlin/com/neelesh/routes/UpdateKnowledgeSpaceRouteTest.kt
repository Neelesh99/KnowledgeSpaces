package com.neelesh.routes

import arrow.core.right
import com.neelesh.persistence.KnowledgeSpaceHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UpdateKnowledgeSpaceRouteTest {

    val knowledgeSpaceHandler = mockk<KnowledgeSpaceHandler>()
    val route = UpdateKnowledgeSpaceRoute(knowledgeSpaceHandler)

    @Test
    fun `will download blob as multipart form`() {
        every { knowledgeSpaceHandler.update(any()) } returns "someFileId".right()

        val request = Request(Method.POST, "/knowledgeSpace/update").body("{\"knowledgeSpaceId\":\"someFileId\",\"email\":\"someEmail\",\"newName\":\"newFileName\",\"newFiles\":[\"someOtherBlobId\"]}")
        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someFileId", response.bodyString())
    }

}