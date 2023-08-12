package com.neelesh.routes

import com.neelesh.llm.QueryRequestHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto

data class SimpleQueryRequest(val email: String, val knowledgeFileTarget: String, val query: String)

// the body lens here is imported as an extension function from the Jackson instance
val simpleQueryRequestLens = Body.auto<SimpleQueryRequest>().toLens()

object QueryRequestRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/queryRequest" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleQueryRequestLens to SimpleQueryRequest("jim@hotmail.com", "hello", "hello"))
        returning(Status.OK, simpleQueryRequestLens to SimpleQueryRequest("jim@hotmail.com", "hello", "hello"))
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(queryRequestHandler: QueryRequestHandler): HttpHandler = { request: Request ->
        val received: SimpleQueryRequest = simpleQueryRequestLens(request)
        val result = queryRequestHandler.handle(received)
        result.fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                Response(Status.OK).body(it)
            }
        ).header("Access-Control-Allow-Origin", "http://localhost:5173")
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Headers", "content-type")
            .header("Access-Control-Allow-Methods", "POST")
    }

    operator fun invoke(queryRequestHandler: QueryRequestHandler): ContractRoute = spec to echo(queryRequestHandler)
}