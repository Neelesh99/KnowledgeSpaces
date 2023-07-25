package com.neelesh.llm

import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.model.IndexRequest
import com.neelesh.model.UserDetails
import org.http4k.core.ContentType
import org.http4k.core.MultipartFormBody
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.InputStream

class LLMIndexRequestBuilderTest {

    @Test
    fun `will generate response from llm request builder`() {
        val email = "someEmail"
        val knowledgeFileTarget = "someFileName"
        val byteInputStream = "someText".byteInputStream()
        val byteInputStreamForExpect = "someText".byteInputStream()
        val blobs = listOf<Pair<BlobReference, InputStream>>(
            BlobReference("someBlobId", DataType.PLAIN_TEXT, "someInfo.txt") to
                    byteInputStream
        )
        val indexRequest = IndexRequest(UserDetails(email), knowledgeFileTarget, blobs.map { it.first })

        val expectedForm = MultipartFormBody()
            .plus("indexRequestFileName" to "indexRequest.json")
            .plus("indexRequest.json" to MultipartFormFile(
                "indexRequest.json",
                ContentType.OCTET_STREAM,
                indexRequest.toJson().toString().byteInputStream()
            ))
            .plus("someInfo.txt" to MultipartFormFile(
                "someInfo.txt",
                ContentType.OCTET_STREAM,
                byteInputStreamForExpect
            ))

        val actual = LLMIndexRequestBuilder.buildIndexRequest(email, knowledgeFileTarget, blobs)

        Assertions.assertEquals(actual.field("indexRequestFileName"), expectedForm.field("indexRequestFileName"))
        Assertions.assertEquals(String(actual.file("indexRequest.json")!!.content.readAllBytes()), String(expectedForm.file("indexRequest.json")!!.content.readAllBytes()))
        val readAllBytes = actual.file("someInfo.txt")!!.content.readAllBytes()
        val readAllBytes1 = expectedForm.file("someInfo.txt")!!.content.readAllBytes()
        Assertions.assertEquals(String(readAllBytes), String(readAllBytes1))

    }




}