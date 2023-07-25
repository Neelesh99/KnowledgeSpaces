package com.neelesh.storage

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.io.File
import java.io.InputStream

class InMemoryBlobStore(val directory: File) : BlobStore {

    private var storageMap = mutableMapOf<String, File>()

    override fun storeBlob(blobId: String, filename: String, content: InputStream) {
        val newFile = File(directory, filename)
        newFile.createNewFile()
        newFile.writeBytes(content.readAllBytes())
        storageMap.put(blobId, newFile)
    }

    override fun getBlob(blobId: String): Either<Exception, Pair<String, InputStream>> {
        val file = storageMap.get(blobId)
        if (file is File) {
            return (file.name to file.inputStream()).right()
        } else {
            return java.lang.Exception("File not found").left()
        }
    }
}