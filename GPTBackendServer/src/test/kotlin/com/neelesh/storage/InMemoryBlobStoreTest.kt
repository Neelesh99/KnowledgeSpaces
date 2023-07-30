package com.neelesh.storage

import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import java.io.File

class InMemoryBlobStoreTest {


    @field:TempDir
    lateinit var testingDirectory: File

    val blobStore: BlobStore by lazy {
        InMemoryBlobStore(testingDirectory)
    }

    @Test
    fun `will store blob`() {
        val content = "someText".byteInputStream()
        val blobId = "someId"
        val filename = "someFileName.txt"
        val blobReference = BlobReference(blobId, DataType.PLAIN_TEXT, filename)
        blobStore.storeBlob(blobReference, content)

        assertEquals("someText", testingDirectory.resolve("someFileName.txt").readText())
    }

    @Test
    fun `will store and read blob`() {
        val content = "someText".byteInputStream()
        val blobId = "someId"
        val filename = "someFileName.txt"

        val blobReference = BlobReference(blobId, DataType.PLAIN_TEXT, filename)
        blobStore.storeBlob(blobReference, content)
        val retrievedBlob = blobStore.getBlob(blobId)
        retrievedBlob.fold(
            {
                fail("should have found file")
            },
            {
                result ->
                    assertEquals(blobReference, result.first)
                    assertEquals("someText", String(result.second.readAllBytes()))
            }
        )

    }

}