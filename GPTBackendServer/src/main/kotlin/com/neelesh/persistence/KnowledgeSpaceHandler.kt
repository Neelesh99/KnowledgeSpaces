package com.neelesh.persistence

import arrow.core.Either
import com.neelesh.model.KnowledgeSpace
import com.neelesh.routes.SimpleKnowledgeSpaceCreationRequest
import com.neelesh.util.UUIDGenerator

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
}
