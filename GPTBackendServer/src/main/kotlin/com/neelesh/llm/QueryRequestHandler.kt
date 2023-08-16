package com.neelesh.llm

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleQueryRequest
import org.http4k.core.*

class QueryRequestHandler(
    val knowledgeFileStore: KnowledgeFileStore,
    val llmClient: HttpHandler
) {
    fun handle(queryRequestDto: SimpleQueryRequest): Either<Exception, String> {
        return knowledgeFileStore.getKnowledgeFile(queryRequestDto.knowledgeFileTarget, queryRequestDto.email)
            .flatMap { knowledgeFile ->
                val indexRequest = LLMQueryRequestBuilder.buildQueryRequest(
                    knowledgeFile,
                    queryRequestDto.query
                )
                val response = llmClient(Request(Method.POST, "http://localhost:2323/api/v1/llm/knowledgeFile/query").body(indexRequest).header("Content-Type", ContentType.MultipartFormWithBoundary(indexRequest.boundary).toHeaderValue()))
                if (response.status != Status.OK) {
                    java.lang.Exception("Error from LLM API code: ${response.status.code}").left()
                } else {
                    response.bodyString().right()
                }
            }
    }
}