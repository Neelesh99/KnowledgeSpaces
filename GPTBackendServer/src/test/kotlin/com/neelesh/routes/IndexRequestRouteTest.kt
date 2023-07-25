package com.neelesh.routes

import arrow.core.left
import arrow.core.right
import com.neelesh.llm.IndexRequestHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.Exception

class IndexRequestRouteTest {

    val indexRequestHandler = mockk<IndexRequestHandler>()
    val route = IndexRequestRoute(indexRequestHandler)

    @Test
    fun `will return error code if index request fails`() {

        every { indexRequestHandler.handle(SimpleIndexRequest("someEmail", "someTarget"))
        } returns Exception("some generic exception").left()

        val request = Request(Method.POST, "/sendRequest").body("{\"email\":\"someEmail\",\"knowledgeFileTarget\":\"someTarget\"}")

        val response = route(request)
        Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR, response.status)
        Assertions.assertEquals("some generic exception", response.bodyString())

    }

    @Test
    fun `will return OK if index request passes`() {

        every { indexRequestHandler.handle(SimpleIndexRequest("someEmail", "someTarget"))
        } returns "someRunId".right()

        val request = Request(Method.POST, "/sendRequest").body("{\"email\":\"someEmail\",\"knowledgeFileTarget\":\"someTarget\"}")

        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someRunId", response.bodyString())

    }

}