package com.neelesh.persistence

import arrow.core.Either
import com.neelesh.model.KnowledgeSpace

interface KnowledgeSpaceStore {

    fun getKnowledgeSpace(knowledgeSpaceId: String, email: String) : Either<Exception, KnowledgeSpace>

    fun saveKnowledgeSpace(knowledgeSpace: KnowledgeSpace) : Either<Exception, KnowledgeSpace>

    fun getSpacesForEmail(email: String) : Either<Exception, List<KnowledgeSpace>>

}