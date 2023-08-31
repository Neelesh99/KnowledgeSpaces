package com.neelesh.persistence

import arrow.core.Either
import arrow.core.flatMap
import com.fasterxml.jackson.databind.JsonNode
import com.neelesh.model.KnowledgeFile
import com.neelesh.routes.SimpleFilesRequest
import com.neelesh.routes.SimpleKnowledgeFileCreationRequest
import com.neelesh.routes.SimpleKnowledgeFileDeletionRequest
import com.neelesh.routes.SimpleKnowledgeFileUpdateRequest
import com.neelesh.util.UUIDGenerator
import org.http4k.format.Jackson

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

    fun update(simpleKnowledgeFileUpdateRequest: SimpleKnowledgeFileUpdateRequest): Either<Exception, String> {
        return knowledgeFileStore
            .getKnowledgeFile(simpleKnowledgeFileUpdateRequest.knowledgeFileId, simpleKnowledgeFileUpdateRequest.email)
            .map { knowledgeFile ->
                val updatedName = simpleKnowledgeFileUpdateRequest.newName ?: knowledgeFile.name
                val updatedBlobIds = simpleKnowledgeFileUpdateRequest.newBlobs ?: knowledgeFile.blobIds
                knowledgeFile.copy(name= updatedName, blobIds = updatedBlobIds)
            }.flatMap { knowledgeFile ->
                knowledgeFileStore.saveKnowledgeFile(knowledgeFile)
            }.map { it.id }

    }

    fun getForEmail(simpleFilesRequest: SimpleFilesRequest) : Either<Exception, JsonNode> {
        return knowledgeFileStore.listFilesForEmail(simpleFilesRequest.email)
            .map { Jackson.array(it.map { file -> file.toDtoJson() }) }
    }

    fun delete(simpleKnowledgeFileDeletionRequest: SimpleKnowledgeFileDeletionRequest) : Either<Exception, Boolean> {
        return knowledgeFileStore.getKnowledgeFile(simpleKnowledgeFileDeletionRequest.knowledgeFileId, simpleKnowledgeFileDeletionRequest.email)
            .flatMap { knowledgeFile: KnowledgeFile -> knowledgeFileStore.deleteKnowledgeFile(knowledgeFile) }
    }

}
