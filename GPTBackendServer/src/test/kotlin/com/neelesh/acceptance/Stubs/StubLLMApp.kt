package com.neelesh.acceptance.Stubs

import com.neelesh.model.IndexRequest
import com.neelesh.model.KnowledgeFile
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.routing.bind
import org.http4k.routing.routes

class StubLLMApp(
    private val responsesForIndexRequests: List<Pair<String, Response>>,
    private val responsesForQueryRequests: List<Pair<String, Response>>
){

    val defaultResponseForIndexRequest = Response(Status.OK).body("{\"runId\":\"someRunId\"}")

    val savedIndexRequests = mutableListOf<Pair<IndexRequest, MultipartFormBody>>()
    val savedQueryRequests = mutableListOf<Pair<KnowledgeFile, String>>()

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
        },
        "/api/v1/llm/knowledgeFile/query" bind Method.POST to {
            val formBody = MultipartFormBody.from(it)
            val knowledgeFileJson = formBody.file("knowledgeFile.json")
            val knowledgeFile = KnowledgeFile.fromJson(Jackson.parse(String(knowledgeFileJson!!.content.readAllBytes())))
            val request = formBody.field("query")!!.value
            savedQueryRequests.add(knowledgeFile to request)
            val response = responsesForQueryRequests.find { targetFileId -> targetFileId.first == knowledgeFile.id }
            response?.second ?: Response(Status.OK).body(request)
        }
    )
}