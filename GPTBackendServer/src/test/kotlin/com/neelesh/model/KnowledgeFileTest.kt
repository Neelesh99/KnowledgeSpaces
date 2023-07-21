package com.neelesh.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KnowledgeFileTest {

    @Test
    fun `will roundtrip knowledge file`() {

        val knowledgeFile = KnowledgeFile(
            "someId",
            "someEmail",
            "someName",
            listOf("someBlobId"),
            "someIndexString"
        )
        Assertions.assertEquals(knowledgeFile, KnowledgeFile.fromJson(knowledgeFile.toJson()))
    }

}