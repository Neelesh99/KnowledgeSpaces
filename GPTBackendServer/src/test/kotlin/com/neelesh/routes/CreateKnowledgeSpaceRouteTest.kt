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

class CreateKnowledgeSpaceRouteTest {

    val knowledgeSpaceHandler = mockk<KnowledgeSpaceHandler>()
    val route = CreateKnowledgeSpaceRoute(knowledgeSpaceHandler)

    @Test
    fun `will download blob as multipart form`() {
        every { knowledgeSpaceHandler.create(any()) } returns "someFileId".right()

        val request = Request(Method.POST, "/knowledgeSpace/create").body("{\"knowledgeSpaceName\":\"someKnowledgeSpaceName\",\"email\":\"someEmail\"}")
        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someFileId", response.bodyString())
    }

}