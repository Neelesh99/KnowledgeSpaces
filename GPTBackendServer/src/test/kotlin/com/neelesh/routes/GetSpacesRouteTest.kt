package com.neelesh.routes

import arrow.core.right
import com.neelesh.persistence.KnowledgeSpaceHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.contract.openapi.OpenAPIJackson.string
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetSpacesRouteTest {

    val knowledgeSpaceHandler = mockk<KnowledgeSpaceHandler>()
    val route = GetSpacesRoute(knowledgeSpaceHandler)

    @Test
    fun `will return OK if index request passes`() {

        every { knowledgeSpaceHandler.getSpaces(SimpleSpacesRequest("someEmail"))
        } returns Jackson.array(listOf(Jackson.obj("id" to string("someId"), "name" to string("someName")))).right()

        val request = Request(Method.POST, "/getSpaces").body("{\"email\":\"someEmail\"}")

        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertTrue(response.bodyString().contains("someId"))

    }

}