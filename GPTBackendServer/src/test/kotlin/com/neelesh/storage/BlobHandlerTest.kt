package com.neelesh.storage

import arrow.core.right
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.model.KnowledgeFile
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleBlobUploadRequest
import com.neelesh.util.UUIDGenerator
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BlobHandlerTest {

    private val blobStore = mockk<BlobStore>()
    private val knowledgeFileStore = mockk<KnowledgeFileStore>()
    private val uuidGenerator = mockk<UUIDGenerator>()

    val blobHandler = BlobHandler(knowledgeFileStore, blobStore, uuidGenerator)

    @Test
    fun `will create a BlobReference from simple blob upload request`() {
        val simpleBlobUpload = SimpleBlobUploadRequest(
            DataType.PLAIN_TEXT,
            "someFileName",
            "someThing".byteInputStream(),
            "someTargetFileId",
            "someEmail"
        )
        every { uuidGenerator.get() } returns "someId"
        val blobReference = blobHandler.getReference(simpleBlobUpload)
        val expected = BlobReference(
            "someId",
            DataType.PLAIN_TEXT,
            "someFileName"
        )
        Assertions.assertEquals(expected, blobReference)
    }

    @Test
    fun `will save blob to storage and update index file`() {
        val simpleBlobUpload = SimpleBlobUploadRequest(DataType.PLAIN_TEXT, "someFileName", "someThing".byteInputStream(), "someTargetFileId", "someEmail")
        every { uuidGenerator.get() } returns "someId"
        val blobReference = BlobReference(
            "someId",
            DataType.PLAIN_TEXT,
            "someFileName"
        )
        justRun { blobStore.storeBlob(blobReference, simpleBlobUpload.dataStream) }
        val knowledgeFile = KnowledgeFile(
            "someTargetFileId",
            "someEmail",
            "someFileName",
            listOf("someBlobId"),
            "{}"
        )
        every { knowledgeFileStore.getKnowledgeFile("someTargetFileId", "someEmail") } returns knowledgeFile.right()

        val expectedNewFile = knowledgeFile.copy(blobIds = listOf("someBlobId", "someId"))
        every { knowledgeFileStore.saveKnowledgeFile(expectedNewFile) } returns expectedNewFile.right()

        Assertions.assertEquals("someTargetFileId".right(), blobHandler.handle(simpleBlobUpload))

    }

}