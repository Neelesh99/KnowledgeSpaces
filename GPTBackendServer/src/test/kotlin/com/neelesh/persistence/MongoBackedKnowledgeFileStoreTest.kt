package com.neelesh.persistence

import arrow.core.right
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.neelesh.model.KnowledgeFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoBackedKnowledgeFileStoreTest {

    val knowledgeFileCollection = mockk<MongoCollection<KnowledgeFile>>()

    val mongoBackedKnowledgeFileStore = MongoBackedKnowledgeFileStore(knowledgeFileCollection)

    @Test
    fun `will get knowledge file from mongo DB`() {
        val fileId = "someFileId"
        val email = "someEmail"
        val knowledgeFile = KnowledgeFile(
            fileId,
            email,
            "someFileName",
            listOf("someBlobId"),
            ""
        )
        every {
            knowledgeFileCollection.findOne(
                org.litote.kmongo.and(KnowledgeFile::id eq fileId, KnowledgeFile::email eq email))
        } returns knowledgeFile

        Assertions.assertEquals(knowledgeFile.right(), mongoBackedKnowledgeFileStore.getKnowledgeFile(fileId, email))
    }

    @Test
    fun `will save knowledge file`() {
        val fileId = "someFileId"
        val email = "someEmail"
        val knowledgeFile = KnowledgeFile(
            fileId,
            email,
            "someFileName",
            listOf("someBlobId"),
            ""
        )
        val replaceOptionsSlot = slot<ReplaceOptions>()
        every {
            knowledgeFileCollection.replaceOne(
                KnowledgeFile::id eq fileId,
                knowledgeFile,
                capture(replaceOptionsSlot)
                )
        } returns UpdateResult.acknowledged(1, 1, null)

        Assertions.assertEquals(knowledgeFile.right(), mongoBackedKnowledgeFileStore.saveKnowledgeFile(knowledgeFile))
        Assertions.assertEquals(true, replaceOptionsSlot.captured.isUpsert)
    }

    @Test
    fun `will delete knowledge file`() {
        val fileId = "someFileId"
        val email = "someEmail"
        val knowledgeFile = KnowledgeFile(
            fileId,
            email,
            "someFileName",
            listOf("someBlobId"),
            ""
        )
        every {
            knowledgeFileCollection.deleteOne(KnowledgeFile::id eq fileId)
        } returns DeleteResult.acknowledged(1)

        Assertions.assertEquals(true.right(), mongoBackedKnowledgeFileStore.deleteKnowledgeFile(knowledgeFile))
    }

}