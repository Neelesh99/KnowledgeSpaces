package com.neelesh.acceptance.Stubs

import com.neelesh.model.IndexRequest
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.routing.bind
import org.http4k.routing.routes

class StubLLMApp(private val responsesForIndexRequests: List<Pair<String, Response>>){

    val defaultResponseForIndexRequest = Response(Status.OK).body("{\"status\":\"done\"}")

    val savedIndexRequests = mutableListOf<Pair<IndexRequest, MultipartFormBody>>()

    fun server() =  routes(
        "/ping" bind Method.GET to {
            Response(Status.OK).body("pong")
        },
        "/api/v1/llm/index" bind Method.POST to {

            val formBody = MultipartFormBody.from(it)
            val indexFileName = formBody.field("indexRequestFileName")!!.value
            val file = formBody.file(indexFileName)
            val indexRequest = IndexRequest.fromJson(Jackson.parse(String(file!!.content.readAllBytes())))
            savedIndexRequests.add(indexRequest to formBody)
            val response = responsesForIndexRequests.find { targetFileId -> targetFileId.first == indexRequest.knowledgeFileTarget }
            response?.second ?: defaultResponseForIndexRequest
        }
    )
}