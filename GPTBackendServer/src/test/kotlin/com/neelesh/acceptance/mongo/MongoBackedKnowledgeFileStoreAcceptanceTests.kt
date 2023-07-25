package com.neelesh.acceptance.mongo

import arrow.core.right
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.neelesh.model.KnowledgeFile
import com.neelesh.persistence.MongoBackedKnowledgeFileStore
import org.junit.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.litote.kmongo.*

@Testcontainers
class MongoBackedKnowledgeFileStoreAcceptanceTests {

    val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
    val connectionString: String by lazy {
        mongoDBContainer.connectionString
    }
    val knowledgeFilesCollection: MongoCollection<KnowledgeFile> by lazy {
        val client = KMongo.createClient(connectionString)
        val database = client.getDatabase("testing")
        database.getCollection<KnowledgeFile>("knowledgeFileCollection")
    }
    val mongoBackedKnowledgeFileStore: MongoBackedKnowledgeFileStore by lazy {
        MongoBackedKnowledgeFileStore(knowledgeFilesCollection)
    }

    @BeforeEach
    fun setup() {
        mongoDBContainer.start()
    }

    @AfterEach
    fun teardown() {
        mongoDBContainer.stop()
    }

    @Test
    fun `will store a basic KnowledgeFile`() {
        val knowledgeFile = KnowledgeFile("someId", "someEmail", "someFileName", listOf("blobId"), "{}")
        mongoBackedKnowledgeFileStore.saveKnowledgeFile(knowledgeFile)

        Assertions.assertEquals(1, knowledgeFilesCollection.countDocuments())
        Assertions.assertEquals(knowledgeFile, knowledgeFilesCollection.findOne())
    }

    @Test
    fun `will retrieve stored knowledge file by id and email`() {
        val knowledgeFile = KnowledgeFile("someId", "someEmail", "someFileOtherName", listOf("blobId"), "{}")
        knowledgeFilesCollection.insertOne(knowledgeFile)
        val retrievedFile = mongoBackedKnowledgeFileStore.getKnowledgeFile("someId", "someEmail")
        Assertions.assertEquals(knowledgeFile.right(), retrievedFile)
    }

}