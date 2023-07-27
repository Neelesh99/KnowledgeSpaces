package com.neelesh.persistence

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import com.neelesh.model.KnowledgeSpace
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.lang.Exception

class MongoBackedKnowledgeSpaceStore(val knowledgeSpaceMongoCollection: MongoCollection<KnowledgeSpace>) : KnowledgeSpaceStore {
    override fun getKnowledgeSpace(knowledgeSpaceId: String, email: String): Either<Exception, KnowledgeSpace> {
        return knowledgeSpaceMongoCollection.findOne(
            and(
                KnowledgeSpace::id eq knowledgeSpaceId,
                KnowledgeSpace::email eq email
            )
        )?.right() ?: Exception("Could not find file").left()
    }

    override fun saveKnowledgeSpace(knowledgeSpace: KnowledgeSpace): Either<Exception, KnowledgeSpace> {
        val result = knowledgeSpaceMongoCollection.replaceOne(
            KnowledgeSpace::id eq knowledgeSpace.id,
            knowledgeSpace,
            ReplaceOptions().upsert(true)
        )
        return if(result.wasAcknowledged()) knowledgeSpace.right() else Exception("Could not save knowledge file").left()
    }

}
