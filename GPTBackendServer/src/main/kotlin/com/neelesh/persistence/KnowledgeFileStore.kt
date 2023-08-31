package com.neelesh.persistence

import arrow.core.Either
import com.neelesh.model.KnowledgeFile

interface KnowledgeFileStore {

    fun getKnowledgeFile(knowledgeFileId: String, email: String) : Either<Exception, KnowledgeFile>

    fun saveKnowledgeFile(knowledgeFile: KnowledgeFile) : Either<Exception, KnowledgeFile>

    fun deleteKnowledgeFile(knowledgeFile: KnowledgeFile) : Either<Exception, Boolean>

    fun listFilesForEmail(email: String) : Either<Exception, List<KnowledgeFile>>

}