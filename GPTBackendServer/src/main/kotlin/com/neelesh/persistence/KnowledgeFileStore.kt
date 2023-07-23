package com.neelesh.persistence

import arrow.core.Either
import com.neelesh.model.KnowledgeFile
import java.lang.Exception

interface KnowledgeFileStore {

    fun getKnowledgeFile(knowledgeFileId: String, email: String) : Either<Exception, KnowledgeFile>

}