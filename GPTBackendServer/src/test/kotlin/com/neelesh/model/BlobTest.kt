package com.neelesh.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BlobTest {

    @Test
    fun `will roundtrip the Blob structure`() {

        val blob = Blob("someId", "someName", "someLink")

        Assertions.assertEquals(blob, Blob.fromJson(blob.toJson()))

    }

}