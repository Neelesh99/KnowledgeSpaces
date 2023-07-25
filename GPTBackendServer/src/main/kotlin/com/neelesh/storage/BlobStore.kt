package com.neelesh.storage

import arrow.core.Either
import java.io.InputStream

interface BlobStore {

    fun storeBlob(blobId: String, filename: String, content: InputStream)

    fun getBlob(blobId: String) : Either<Exception, Pair<String, InputStream>>

}