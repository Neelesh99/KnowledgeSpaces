package com.neelesh.persistence

import arrow.core.Either
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import java.lang.Exception

interface KnowledgeSpaceStore {

    fun getKnowledgeSpace(knowledgeSpaceId: String, email: String) : Either<Exception, KnowledgeSpace>

    fun saveKnowledgeSpace(knowledgeSpace: KnowledgeSpace) : Either<Exception, KnowledgeSpace>

}