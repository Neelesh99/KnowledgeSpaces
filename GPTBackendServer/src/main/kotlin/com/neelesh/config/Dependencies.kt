package com.neelesh.config

import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.storage.BlobStore
import org.http4k.core.HttpHandler

data class Dependencies(
    val llmClient: HttpHandler,
    val blobStore: BlobStore,
    val knowledgeFileStore: KnowledgeFileStore
)