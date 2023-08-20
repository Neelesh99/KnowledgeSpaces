package com.neelesh.storage

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.mongodb.client.MongoCollection
import com.mongodb.client.result.InsertOneResult
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.io.InputStream

class GoogleBlobStoreTest {

    private val storage = mockk<Storage>()
    private val blobReferenceCollection = mockk<MongoCollection<BlobReference>>()
    val blobStore: BlobStore = GoogleBlobStore(storage, blobReferenceCollection, "someBucketName")

    @Test
    fun `will store blob`() {
        val content = "someText".byteInputStream()
        val blobId = "someId"
        val filename = "someFileName.txt"
        val blobReference = BlobReference(blobId, DataType.PLAIN_TEXT, filename)
        val infoCapture = slot<BlobInfo>()
        val contentCapture = slot<InputStream>()
        val responseBlob = mockk<Blob>()
        every { storage.createFrom(capture(infoCapture), capture(contentCapture)) } returns responseBlob
        every { blobReferenceCollection.insertOne(blobReference) } returns InsertOneResult.acknowledged(null)
        blobStore.storeBlob(blobReference, content)
        Assertions.assertEquals("someBucketName", infoCapture.captured.bucket)
        Assertions.assertEquals("someId", infoCapture.captured.blobId.name)
        Assertions.assertEquals("someText", String(contentCapture.captured.readAllBytes()))
    }

    @Test
    fun `will retrieve blob`() {
        val infoCapture = slot<BlobId>()
        val blobId = "someId"
        val filename = "someFileName.txt"
        val blobReference = BlobReference(blobId, DataType.PLAIN_TEXT, filename)
        every { blobReferenceCollection.findOne(BlobReference::blobId eq blobId) } returns blobReference
        every { storage.readAllBytes(capture(infoCapture)) } returns "someText".toByteArray()

        val result = blobStore.getBlob(blobId)
        result.fold(
            {
                fail("should not have thrown exception")
            }, {
                Assertions.assertEquals(blobReference, it.first)
                Assertions.assertEquals("someText", String(it.second.readAllBytes()))
            }
        )

    }
}