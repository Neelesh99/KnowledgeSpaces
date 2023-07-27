package com.neelesh.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KnowledgeSpaceTest {

    @Test
    fun `will roundtrip knowledgeSpace`() {
        val knowledgeSpace = KnowledgeSpace(
            "someId",
            "someName",
            "someEmail",
            listOf("someFileId")
        )

        Assertions.assertEquals(knowledgeSpace, KnowledgeSpace.fromJson(knowledgeSpace.toJson()))
    }

}