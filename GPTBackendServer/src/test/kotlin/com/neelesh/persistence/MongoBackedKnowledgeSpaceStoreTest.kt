package com.neelesh.persistence

import arrow.core.right
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.result.UpdateResult
import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoBackedKnowledgeSpaceStoreTest {

    val knowledgeSpaceMongoCollection = mockk<MongoCollection<KnowledgeSpace>>()

    val mongoBackedKnowledgeSpaceStore = MongoBackedKnowledgeSpaceStore(knowledgeSpaceMongoCollection)

    @Test
    fun `will get knowledge file from mongo DB`() {
        val spaceId = "someFileId"
        val email = "someEmail"
        val knowledgeSpace = KnowledgeSpace(
            spaceId,
            "someFileName",
            email,
            listOf("someFileId")
        )
        every {
            knowledgeSpaceMongoCollection.findOne(
                org.litote.kmongo.and(KnowledgeFile::id eq spaceId, KnowledgeFile::email eq email))
        } returns knowledgeSpace

        Assertions.assertEquals(knowledgeSpace.right(), mongoBackedKnowledgeSpaceStore.getKnowledgeSpace(spaceId, email))
    }

    @Test
    fun `will save knowledge file`() {
        val spaceId = "someFileId"
        val email = "someEmail"
        val knowledgeSpace = KnowledgeSpace(
            spaceId,
            "someFileName",
            email,
            listOf("someFileId")
        )
        val replaceOptionsSlot = slot<ReplaceOptions>()
        every {
            knowledgeSpaceMongoCollection.replaceOne(
                KnowledgeSpace::id eq spaceId,
                knowledgeSpace,
                capture(replaceOptionsSlot)
                )
        } returns UpdateResult.acknowledged(1, 1, null)

        Assertions.assertEquals(knowledgeSpace.right(), mongoBackedKnowledgeSpaceStore.saveKnowledgeSpace(knowledgeSpace))
        Assertions.assertEquals(true, replaceOptionsSlot.captured.isUpsert)
    }

}