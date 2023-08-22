package com.neelesh.acceptance.Stubs

import com.neelesh.model.IndexRequest
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import org.http4k.core.Method
import org.http4k.core.MultipartFormBody
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.bind
import org.http4k.routing.routes

class StubLLMApp(
    private val responsesForIndexRequests: List<Pair<String, Response>>,
    private val responsesForQueryRequests: List<Pair<String, Response>>,
    private val responsesForSpacesQueryRequests: List<Pair<String, Response>>
){

    val defaultResponseForIndexRequest = Response(Status.OK).body("{\"runId\":\"someRunId\"}")

    val savedIndexRequests = mutableListOf<Pair<IndexRequest, MultipartFormBody>>()
    val savedQueryRequests = mutableListOf<Pair<KnowledgeFile, String>>()
    val savedSpacesQueryRequests = mutableListOf<Pair<KnowledgeSpace, String>>()

    fun server() =  routes(
        "/ping" bind Method.GET to {
            Response(Status.OK).body("pong")
        },
        "api/v1/llm/index" bind Method.POST to {

            val formBody = MultipartFormBody.from(it)
            val indexFileName = formBody.field("indexRequestFileName")!!.value
            val file = formBody.file(indexFileName)
            val indexRequest = IndexRequest.fromJson(Jackson.parse(String(file!!.content.readAllBytes())))
            savedIndexRequests.add(indexRequest to formBody)
            val response = responsesForIndexRequests.find { targetFileId -> targetFileId.first == indexRequest.knowledgeFileTarget }
            response?.second ?: defaultResponseForIndexRequest
        },
        "api/v1/llm/knowledgeFile/query" bind Method.POST to {
            val formBody = MultipartFormBody.from(it)
            val knowledgeFileJson = formBody.file("knowledgeFile.json")
            val knowledgeFile = KnowledgeFile.fromJson(Jackson.parse(String(knowledgeFileJson!!.content.readAllBytes())))
            val request = formBody.field("query")!!.value
            savedQueryRequests.add(knowledgeFile to request)
            val response = responsesForQueryRequests.find { targetFileId -> targetFileId.first == knowledgeFile.id }
            response?.second ?: Response(Status.OK).body(request)
        },
        "api/v1/llm/knowledgeSpace/query" bind Method.POST to { queryRequest ->
            val formBody = MultipartFormBody.from(queryRequest)
            val knowledgeSpaceJson = formBody.file("knowledgeSpace.json")
            val knowledgeSpace = KnowledgeSpace.fromJson(Jackson.parse(String(knowledgeSpaceJson!!.content.readAllBytes())))
            val knowledgeFileIds = knowledgeSpace.files
            val knowledgeFilesJsons = knowledgeFileIds.map { id -> String(formBody.file(id)!!.content.readAllBytes()) }
            val knowledgeFiles = knowledgeFilesJsons.map { KnowledgeFile.fromJson(Jackson.parse(it)) }
            val request = formBody.field("query")!!.value
            savedSpacesQueryRequests.add(knowledgeSpace to request)
            val response = responsesForSpacesQueryRequests.find { targetSpaceId -> targetSpaceId.first == knowledgeSpace.id }
            response?.second ?: Response(Status.OK).body(request)
        }
    )
}