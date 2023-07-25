package com.neelesh.llm

import arrow.core.*
import arrow.core.raise.either
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleIndexRequest
import com.neelesh.storage.BlobStore
import org.http4k.core.*
import org.http4k.format.Jackson

class IndexRequestHandler(
    val blobStore: BlobStore,
    val knowledgeFileStore: KnowledgeFileStore,
    val llmClient: HttpHandler
) {
    fun handle(indexRequestDto: SimpleIndexRequest): Either<Exception, String> {

        // check knowledge file exists
        val knowledgeFileResponse =
            knowledgeFileStore.getKnowledgeFile(indexRequestDto.knowledgeFileTarget, indexRequestDto.email)
        return knowledgeFileResponse.map { knowledgeFile ->
            knowledgeFile.blobIds.map { blobStore.getBlob(it) }
        }.flatMap { maybeBlobs ->
            either { maybeBlobs.map { (it).bind() } }
        }.flatMap { blobs ->
            val indexRequest = LLMIndexRequestBuilder.buildIndexRequest(
                indexRequestDto.email,
                indexRequestDto.knowledgeFileTarget,
                blobs
            )
            val response = llmClient(Request(Method.POST, "/api/v1/llm/index").body(indexRequest).header("Content-Type", ContentType.MultipartFormWithBoundary(indexRequest.boundary).toHeaderValue()))
            if (response.status != Status.OK) {
                java.lang.Exception("Error from LLM API code: ${response.status.code}").left()
            } else {
                val bodyStringParsed = Jackson.parse(response.bodyString())
                bodyStringParsed.get("runId").textValue().right()
            }
        }
    }


}