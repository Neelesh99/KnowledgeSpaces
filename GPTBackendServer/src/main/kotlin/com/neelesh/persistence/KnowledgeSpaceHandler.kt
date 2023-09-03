package com.neelesh.persistence

import arrow.core.Either
import arrow.core.flatMap
import com.fasterxml.jackson.databind.JsonNode
import com.neelesh.model.KnowledgeSpace
import com.neelesh.routes.SimpleKnowledgeSpaceCreationRequest
import com.neelesh.routes.SimpleKnowledgeSpaceUpdateRequest
import com.neelesh.routes.SimpleSpacesRequest
import com.neelesh.util.UUIDGenerator
import org.http4k.format.Jackson

class KnowledgeSpaceHandler(
    val knowledgeSpaceStore: KnowledgeSpaceStore,
    val uuidGenerator: UUIDGenerator
) {
    fun create(simpleKnowledgeSpaceCreationRequest: SimpleKnowledgeSpaceCreationRequest): Either<Exception, String> {
        val knowledgeSpaceId = uuidGenerator.get()
        val knowledgeSpace = KnowledgeSpace(
            knowledgeSpaceId,
            simpleKnowledgeSpaceCreationRequest.knowledgeSpaceName,
            simpleKnowledgeSpaceCreationRequest.email,
            emptyList()
        )
        return knowledgeSpaceStore.saveKnowledgeSpace(knowledgeSpace).map { it.id }
    }

    fun update(simpleKnowledgeSpaceUpdateRequest: SimpleKnowledgeSpaceUpdateRequest): Either<Exception, String> {
        return knowledgeSpaceStore.getKnowledgeSpace(
            simpleKnowledgeSpaceUpdateRequest.knowledgeSpaceId,
            simpleKnowledgeSpaceUpdateRequest.email
        ).flatMap { oldKnowledgeSpace ->
            val updatedName = simpleKnowledgeSpaceUpdateRequest.newName ?: oldKnowledgeSpace.name
            val updatedFileIds = simpleKnowledgeSpaceUpdateRequest.newFiles ?: oldKnowledgeSpace.files
            val newKnowledgeSpace = oldKnowledgeSpace.copy(name = updatedName, files = updatedFileIds)
            knowledgeSpaceStore.saveKnowledgeSpace(newKnowledgeSpace)
        }.map {
            it.id
        }
    }

    fun getSpaces(simpleKnowledgeSpacesRequest: SimpleSpacesRequest): Either<Exception, JsonNode> {
        return knowledgeSpaceStore.getSpacesForEmail(simpleKnowledgeSpacesRequest.email)
            .map { spaces ->
                spaces.map { it.toJson() }
            }.map {
                Jackson.array(it)
            }
    }
}
