package com.neelesh.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LLMInterfaceTest {

    val blobReference = BlobReference(
        "someBlobId",
        DataType.PLAIN_TEXT,
        "someFileName"
    )

    val userDetails = UserDetails(
        "someEmail"
    )

    @Test
    fun `will roundtrip BlobData`() {


        Assertions.assertEquals(blobReference, BlobReference.fromJson(blobReference.toJson()))
    }

    @Test
    fun `will roundtrip UserDetails`() {

        Assertions.assertEquals(userDetails, UserDetails.fromJson(userDetails.toJson()))
    }

    @Test
    fun `will roundtrip index request`() {
        val indexRequest = IndexRequest(
            UserDetails("someEmail"),
            "someKnowledgeFile",
            listOf(blobReference)
        )

        Assertions.assertEquals(indexRequest, IndexRequest.fromJson(indexRequest.toJson()))
    }
}