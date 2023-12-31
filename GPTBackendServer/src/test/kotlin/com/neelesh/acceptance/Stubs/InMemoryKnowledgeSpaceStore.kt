package com.neelesh.acceptance.Stubs

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.neelesh.model.KnowledgeSpace
import com.neelesh.persistence.KnowledgeSpaceStore

class InMemoryKnowledgeSpaceStore : KnowledgeSpaceStore {

    val fileStore = mutableListOf<KnowledgeSpace>()

    override fun getKnowledgeSpace(knowledgeSpaceId: String, email: String): Either<Exception, KnowledgeSpace> {
        val foundFile = fileStore.find { file -> file.id == knowledgeSpaceId && file.email == email }
        return if(foundFile is KnowledgeSpace) foundFile.right() else Exception("Cannot find file with those details in store").left()
    }

    override fun saveKnowledgeSpace(knowledgeSpace: KnowledgeSpace): Either<Exception, KnowledgeSpace> {
        val indexOfFirst = fileStore.indexOfFirst { file -> file.id == knowledgeSpace.id }
        if (indexOfFirst != -1){
            fileStore.set(indexOfFirst, knowledgeSpace)
        } else {
            fileStore.add(knowledgeSpace)
        }
        return knowledgeSpace.right()
    }

    override fun getSpacesForEmail(email: String): Either<java.lang.Exception, List<KnowledgeSpace>> {
        return fileStore.filter { space -> space.email == email }.right()
    }
}