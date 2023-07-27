package com.neelesh.routes

import com.neelesh.llm.QueryRequestHandler
import com.neelesh.llm.SpacesQueryRequestHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto

data class SimpleSpaceQueryRequest(val email: String, val knowledgeSpaceTarget: String, val query: String)

// the body lens here is imported as an extension function from the Jackson instance
val simpleSpaceQueryRequestLens = Body.auto<SimpleSpaceQueryRequest>().toLens()

object SpaceQueryRequestRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/space/queryRequest" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleSpaceQueryRequestLens to SimpleSpaceQueryRequest("jim@hotmail.com", "hello", "hello"))
        returning(Status.OK, simpleSpaceQueryRequestLens to SimpleSpaceQueryRequest("jim@hotmail.com", "hello", "hello"))
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(queryRequestHandler: SpacesQueryRequestHandler): HttpHandler = { request: Request ->
        val received: SimpleSpaceQueryRequest = simpleSpaceQueryRequestLens(request)
        val result = queryRequestHandler.handle(received)
        result.fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                Response(Status.OK).body(it)
            }
        )
    }

    operator fun invoke(queryRequestHandler: SpacesQueryRequestHandler): ContractRoute = spec to echo(queryRequestHandler)
}