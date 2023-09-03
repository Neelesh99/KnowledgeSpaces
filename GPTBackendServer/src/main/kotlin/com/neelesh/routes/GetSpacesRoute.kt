package com.neelesh.routes

import com.neelesh.persistence.KnowledgeSpaceHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.string

data class SimpleSpacesRequest(val email: String)

// the body lens here is imported as an extension function from the Jackson instance
val simpleSpacesRequest = Body.auto<SimpleSpacesRequest>().toLens()

object GetSpacesRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/getSpaces" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleSpacesRequest to SimpleSpacesRequest("jim@hotmail.com"))
        returning(Status.OK, simpleSpacesRequest to SimpleSpacesRequest("jim@hotmail.com"))
    } bindContract Method.POST

    private val DEFAULT = Jackson.array(listOf(Jackson.obj("id" to string("someId"), "name" to string("A cool filename"))))

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(knowledgeSpaceHandler: KnowledgeSpaceHandler): HttpHandler = { request: Request ->
        val received: SimpleSpacesRequest = simpleSpacesRequest(request)
        val result = knowledgeSpaceHandler.getSpaces(received)
        result.fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                json -> Response(Status.OK).body(json.toString())
            }
        )
    }

    operator fun invoke(knowledgeSpaceHandler: KnowledgeSpaceHandler): ContractRoute = spec to echo(knowledgeSpaceHandler)
}