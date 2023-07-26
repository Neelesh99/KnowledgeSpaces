package com.neelesh.llm

import arrow.core.Either
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleQueryRequest
import com.neelesh.storage.BlobStore
import io.undertow.server.HttpHandler

class QueryRequestHandler(
    val blobStore: BlobStore,
    val knowledgeFileStore: KnowledgeFileStore,
    val llmClient: HttpHandler
) {
    fun handle(queryRequestDto: SimpleQueryRequest): Either<Exception, String> {
        TODO("Not yet implemented")
    }
}