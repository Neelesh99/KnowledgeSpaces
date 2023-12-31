package com.neelesh.storage

import arrow.core.Either
import arrow.core.flatMap
import com.neelesh.model.BlobReference
import com.neelesh.model.KnowledgeFile
import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.routes.SimpleBlobDownloadRequest
import com.neelesh.routes.SimpleBlobUploadRequest
import com.neelesh.util.UUIDGenerator
import java.io.InputStream

class BlobHandler(
    val knowledgeFileStore: KnowledgeFileStore,
    val blobStore: BlobStore,
    val uuidGenerator: UUIDGenerator
) {
    fun getReference(simpleBlobUpload: SimpleBlobUploadRequest): BlobReference {
        return BlobReference(
            uuidGenerator.get(),
            simpleBlobUpload.type,
            simpleBlobUpload.fileName
        )
    }

    fun upload(simpleBlobUpload: SimpleBlobUploadRequest): Either<Exception, String> {
        val blobReference = getReference(simpleBlobUpload)
        blobStore.storeBlob(blobReference, simpleBlobUpload.dataStream)
        return knowledgeFileStore.getKnowledgeFile(simpleBlobUpload.knowledgeFileTarget, simpleBlobUpload.email)
            .flatMap { knowledgeFile: KnowledgeFile ->
                val updatedFile = knowledgeFile.copy(blobIds = knowledgeFile.blobIds + blobReference.blobId)
                knowledgeFileStore.saveKnowledgeFile(updatedFile)
            }
            .map { it.id }
    }

    fun download(simpleBlobDownload: SimpleBlobDownloadRequest): Either<Exception, Pair<BlobReference, InputStream>> {
        return blobStore.getBlob(simpleBlobDownload.blobId)
    }

}
