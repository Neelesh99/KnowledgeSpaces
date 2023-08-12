package com.neelesh.routes

import com.neelesh.persistence.KnowledgeFileHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto

data class SimpleFilesRequest(val email: String)

// the body lens here is imported as an extension function from the Jackson instance
val simpleFilesRequest = Body.auto<SimpleFilesRequest>().toLens()

object GetFilesRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/getFiles" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleFilesRequest to SimpleFilesRequest("jim@hotmail.com"))
        returning(Status.OK, simpleFilesRequest to SimpleFilesRequest("jim@hotmail.com"))
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(knowledgeFileHandler: KnowledgeFileHandler): HttpHandler = { request: Request ->
        val received: SimpleFilesRequest = simpleFilesRequest(request)
        val result = knowledgeFileHandler.getForEmail(received)
        result.fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                json -> Response(Status.OK).body(json.toString())
            }
        )
    }

    operator fun invoke(knowledgeFileHandler: KnowledgeFileHandler): ContractRoute = spec to echo(knowledgeFileHandler)
}