package com.neelesh.storage

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.neelesh.model.BlobReference
import java.io.File
import java.io.InputStream

class InMemoryBlobStore(val directory: File) : BlobStore {

    private var storageMap = mutableMapOf<BlobReference, File>()

    override fun storeBlob(blobReference: BlobReference, content: InputStream) {
        val newFile = File(directory, blobReference.fileName)
        newFile.createNewFile()
        newFile.writeBytes(content.readAllBytes())
        storageMap.put(blobReference, newFile)
    }

    override fun getBlob(blobId: String): Either<Exception, Pair<BlobReference, InputStream>> {
        val filteredMap = storageMap.filter { entry -> entry.key.blobId == blobId }
        val blobReference = filteredMap.keys.find { true }
        val file = filteredMap.values.find { true }
        if (file is File) {
            return (blobReference!! to file.inputStream()).right()
        } else {
            return java.lang.Exception("File not found").left()
        }
    }
}