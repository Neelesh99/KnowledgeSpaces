package com.neelesh.config

import com.neelesh.persistence.KnowledgeFileStore
import com.neelesh.persistence.KnowledgeSpaceStore
import com.neelesh.storage.BlobStore
import org.http4k.core.HttpHandler
import org.http4k.security.OAuthPersistence

data class Dependencies(
    val llmClient: HttpHandler,
    val blobStore: BlobStore,
    val knowledgeFileStore: KnowledgeFileStore,
    val knowledgeSpaceStore: KnowledgeSpaceStore,
    val oAuthPersistence: OAuthPersistence
)