package com.neelesh.storage

import arrow.core.Either
import com.neelesh.model.BlobReference
import java.io.InputStream

interface BlobStore {

    fun storeBlob(blobReference: BlobReference, content: InputStream)

    fun getBlob(blobId: String) : Either<Exception, Pair<BlobReference, InputStream>>

}