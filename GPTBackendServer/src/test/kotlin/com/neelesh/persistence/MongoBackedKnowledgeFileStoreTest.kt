package com.neelesh.persistence

import arrow.core.left
import arrow.core.right
import com.mongodb.client.MongoCollection
import com.neelesh.model.KnowledgeFile
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.litote.kmongo.and
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

}