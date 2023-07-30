package com.neelesh.persistence

import arrow.core.Either
import com.neelesh.model.KnowledgeFile
import com.neelesh.routes.SimpleKnowledgeFileCreationRequest
import com.neelesh.util.UUIDGenerator

class KnowledgeFileHandler(
    val knowledgeFileStore: KnowledgeFileStore,
    val uuidGenerator: UUIDGenerator
) {
    fun create(simpleKnowledgeFileCreationRequest: SimpleKnowledgeFileCreationRequest): Either<Exception, String> {
        val knowledgeFileId = uuidGenerator.get()
        val knowledgeFile = KnowledgeFile(
            knowledgeFileId,
            simpleKnowledgeFileCreationRequest.email,
            simpleKnowledgeFileCreationRequest.knowledgeFileName,
            emptyList(),
            "{}"
        )
        return knowledgeFileStore.saveKnowledgeFile(knowledgeFile).map { it.id }
    }

}
