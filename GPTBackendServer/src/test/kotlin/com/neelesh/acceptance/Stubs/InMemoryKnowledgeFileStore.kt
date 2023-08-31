package com.neelesh.acceptance.Stubs

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.neelesh.model.KnowledgeFile
import com.neelesh.persistence.KnowledgeFileStore

class InMemoryKnowledgeFileStore(val existingFiles: List<KnowledgeFile>) : KnowledgeFileStore {

    val fileStore = MutableList(existingFiles.size) {index -> existingFiles[index]}

    override fun getKnowledgeFile(knowledgeFileId: String, email: String): Either<Exception, KnowledgeFile> {
        val foundFile = fileStore.find { file -> file.id == knowledgeFileId && file.email == email }
        return if(foundFile is KnowledgeFile) foundFile.right() else Exception("Cannot find file with those details in store").left()
    }

    override fun saveKnowledgeFile(knowledgeFile: KnowledgeFile): Either<Exception, KnowledgeFile> {
        val indexOfFirst = fileStore.indexOfFirst { file -> file.id == knowledgeFile.id }
        if (indexOfFirst != -1){
            fileStore.set(indexOfFirst, knowledgeFile)
        } else {
            fileStore.add(knowledgeFile)
        }
        return knowledgeFile.right()
    }

    override fun deleteKnowledgeFile(knowledgeFile: KnowledgeFile): Either<Exception, Boolean> {
        val indexOfFirst = fileStore.indexOfFirst { file -> file.id == knowledgeFile.id }
        if (indexOfFirst != -1){
            fileStore.removeAt(indexOfFirst)
            return true.right()
        } else {
            return false.right()
        }
    }

    override fun listFilesForEmail(email: String): Either<Exception, List<KnowledgeFile>> {
        return fileStore.filter { knowledgeFile -> knowledgeFile.email == email }.right()
    }


}