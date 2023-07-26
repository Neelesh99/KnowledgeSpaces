package com.neelesh.llm

import com.neelesh.model.*
import org.http4k.core.ContentType
import org.http4k.core.MultipartFormBody
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.InputStream

class LLMQueryRequestBuilderTest {

    @Test
    fun `will generate response from llm request builder`() {
        val knowledgeFile = KnowledgeFile(
            "someKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        val query = "someQuery"

        val expectedForm = MultipartFormBody()
            .plus("knowledgeFile.json" to MultipartFormFile(
                "knowledgeFile.json",
                ContentType.OCTET_STREAM,
                knowledgeFile.toJson().toString().byteInputStream()))
            .plus("query" to query)

        val actual = LLMQueryRequestBuilder.buildQueryRequest(knowledgeFile, query)

        Assertions.assertEquals(actual.field("query"), expectedForm.field("query"))
        Assertions.assertEquals(String(actual.file("knowledgeFile.json")!!.content.readAllBytes()), String(expectedForm.file("knowledgeFile.json")!!.content.readAllBytes()))

    }

}