package com.neelesh.routes

import com.neelesh.persistence.KnowledgeSpaceHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto

data class SimpleKnowledgeSpaceCreationRequest(
    val knowledgeSpaceName: String,
    val email: String
)

// the body lens here is imported as an extension function from the Jackson instance
val simpleKnowledgeSpaceCreationRequest = Body.auto<SimpleKnowledgeSpaceCreationRequest>().toLens()

object CreateKnowledgeSpaceRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/knowledgeSpace/create" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleKnowledgeSpaceCreationRequest to SimpleKnowledgeSpaceCreationRequest("knowledgeFileName", "someEmail"))
        returning(Status.OK, simpleKnowledgeSpaceCreationRequest to SimpleKnowledgeSpaceCreationRequest("knowledgeFileName", "someEmail"))
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(knowledgeSpaceHandler: KnowledgeSpaceHandler): HttpHandler = { request: Request ->
        val received = simpleKnowledgeSpaceCreationRequest(request)
        val result = knowledgeSpaceHandler.create(received)
        result.fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                Response(Status.OK).body(it)
            }
        )
    }

    operator fun invoke(knowledgeSpaceHandler: KnowledgeSpaceHandler): ContractRoute = spec to echo(knowledgeSpaceHandler)
}