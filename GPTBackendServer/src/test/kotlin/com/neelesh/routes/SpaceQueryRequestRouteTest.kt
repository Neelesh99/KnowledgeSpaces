package com.neelesh.routes

import arrow.core.left
import arrow.core.right
import com.neelesh.llm.QueryRequestHandler
import com.neelesh.llm.SpacesQueryRequestHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.Exception

class SpaceQueryRequestRouteTest {

    val queryRequestHandler = mockk<SpacesQueryRequestHandler>()
    val route = SpaceQueryRequestRoute(queryRequestHandler)

    @Test
    fun `will return error code if index request fails`() {

        every { queryRequestHandler.handle(SimpleSpaceQueryRequest("someEmail", "someTarget", "someQuery"))
        } returns Exception("some generic exception").left()

        val request = Request(Method.POST, "/space/queryRequest").body("{\"email\":\"someEmail\",\"knowledgeSpaceTarget\":\"someTarget\",\"query\":\"someQuery\"}")

        val response = route(request)
        Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR, response.status)
        Assertions.assertEquals("some generic exception", response.bodyString())

    }

    @Test
    fun `will return OK if index request passes`() {

        every { queryRequestHandler.handle(SimpleSpaceQueryRequest("someEmail", "someTarget", "someQuery"))
        } returns "hello".right()

        val request = Request(Method.POST, "/space/queryRequest").body("{\"email\":\"someEmail\",\"knowledgeSpaceTarget\":\"someTarget\",\"query\":\"someQuery\"}")

        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("hello", response.bodyString())

    }
}