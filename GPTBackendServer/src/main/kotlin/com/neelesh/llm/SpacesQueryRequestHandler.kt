package com.neelesh.llm

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.persistence.KnowledgeSpaceStore
import com.neelesh.routes.SimpleQueryRequest
import com.neelesh.routes.SimpleSpaceQueryRequest
import org.http4k.core.*
import org.http4k.format.Jackson

class SpacesQueryRequestHandler(
    val knowledgeFileStore: KnowledgeFileStore,
    val knowledgeSpaceStore: KnowledgeSpaceStore,
    val llmClient: HttpHandler
) {
    fun handle(queryRequestDto: SimpleSpaceQueryRequest): Either<Exception, String> {
        return knowledgeSpaceStore.getKnowledgeSpace(queryRequestDto.knowledgeSpaceTarget, queryRequestDto.email)
            .map { knowledgeSpace ->
                val fileIds = knowledgeSpace.files
                knowledgeSpace to fileIds.map { knowledgeFileStore.getKnowledgeFile(it, queryRequestDto.email) }
            }.flatMap { knowledgeSpaceListPair ->
                either {
                    knowledgeSpaceListPair.first to knowledgeSpaceListPair.second.map { (it).bind() }
                }
            }.flatMap { spaceToListOfFiles ->
                val requestBody = LLMSpacesQueryRequestBuilder.buildQueryRequest(spaceToListOfFiles.first, spaceToListOfFiles.second, queryRequestDto.query)
                val response = llmClient(Request(Method.POST, "/api/v1/llm/knowledgeSpace/query").body(requestBody).header("Content-Type", ContentType.MultipartFormWithBoundary(requestBody.boundary).toHeaderValue()))
                if (response.status != Status.OK) {
                    java.lang.Exception("Error from LLM API code: ${response.status.code}").left()
                } else {
                    response.bodyString().right()
                }
            }
    }
}