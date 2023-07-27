package com.neelesh.acceptance.Stubs

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import com.neelesh.persistence.KnowledgeSpaceStore
import java.lang.Exception

class InMemoryKnowledgeSpaceStore : KnowledgeSpaceStore {

    val fileStore = mutableListOf<KnowledgeSpace>()

    override fun getKnowledgeSpace(knowledgeSpaceId: String, email: String): Either<Exception, KnowledgeSpace> {
        val foundFile = fileStore.find { file -> file.id == knowledgeSpaceId && file.email == email }
        return if(foundFile is KnowledgeSpace) foundFile.right() else Exception("Cannot find file with those details in store").left()
    }

    override fun saveKnowledgeSpace(knowledgeSpace: KnowledgeSpace): Either<Exception, KnowledgeSpace> {
        fileStore.add(knowledgeSpace)
        return knowledgeSpace.right()
    }
}