package com.neelesh.storage

import arrow.core.Either
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.mongodb.client.MongoCollection
import com.neelesh.model.BlobReference
import java.io.InputStream

class GoogleBlobStore(
    val storage: Storage,
    val blobReferenceCollection: MongoCollection<BlobReference>,
    val bucketName: String
) : BlobStore {
    override fun storeBlob(blobReference: BlobReference, content: InputStream) {
        blobReferenceCollection.insertOne(blobReference)
        val blobId = BlobId.of(bucketName, blobReference.blobId)
        val blobInfo = BlobInfo.newBuilder(blobId).build()
        storage.createFrom(blobInfo, content)
    }

    override fun getBlob(blobId: String): Either<Exception, Pair<BlobReference, InputStream>> {
        TODO("Not yet implemented")
    }


}
