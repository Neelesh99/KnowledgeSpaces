package com.neelesh.persistence

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import com.neelesh.model.KnowledgeFile
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.lang.Exception

class MongoBackedKnowledgeFileStore(private val knowledgeFileCollection: MongoCollection<KnowledgeFile>) : KnowledgeFileStore {
    override fun getKnowledgeFile(knowledgeFileId: String, email: String): Either<Exception, KnowledgeFile> {
        return knowledgeFileCollection.findOne(
            and(
                KnowledgeFile::id eq knowledgeFileId,
                KnowledgeFile::email eq email
            )
        )?.right() ?: Exception("Could not find file").left()
    }

    override fun saveKnowledgeFile(knowledgeFile: KnowledgeFile): Either<Exception, KnowledgeFile> {
        val result = knowledgeFileCollection.replaceOne(
            KnowledgeFile::id eq knowledgeFile.id,
            knowledgeFile,
            ReplaceOptions().upsert(true)
        )
        return if(result.wasAcknowledged()) knowledgeFile.right() else Exception("Could not save knowledge file").left()
    }
}