package com.neelesh.routes

import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto

data class SimpleIndexRequest(val email: String, val knowledgeFileTarget: String)

// the body lens here is imported as an extension function from the Jackson instance
val simpleIndexRequestLens = Body.auto<SimpleIndexRequest>().toLens()

object IndexRequestRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/sendRequest" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleIndexRequestLens to SimpleIndexRequest("jim@hotmail.com", "hello"))
        returning(Status.OK, simpleIndexRequestLens to SimpleIndexRequest("jim@hotmail.com", "hello"))
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private val echo: HttpHandler = { request: Request ->
        val received: SimpleIndexRequest = simpleIndexRequestLens(request)
        Response(Status.OK).with(simpleIndexRequestLens of received)
    }

    operator fun invoke(): ContractRoute = spec to echo
}