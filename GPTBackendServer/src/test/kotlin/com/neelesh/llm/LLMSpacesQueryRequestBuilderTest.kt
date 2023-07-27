package com.neelesh.llm

import com.neelesh.model.*
import org.http4k.core.ContentType
import org.http4k.core.MultipartFormBody
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.InputStream

class LLMSpacesQueryRequestBuilderTest {

    @Test
    fun `will generate response from llm spaces request builder`() {
        val knowledgeSpace = KnowledgeSpace(
            "someSpaceId",
            "someSpaceName",
            "someEmail",
            listOf("someKnowledgeFileId")
        )
        val knowledgeFile = KnowledgeFile(
            "someKnowledgeFileId",
            "someEmail",
            "someKnowledgeFileName",
            listOf("someBlobId"),
            "{}"
        )
        val query = "someQuery"

        val expectedForm = MultipartFormBody()
            .plus("knowledgeSpace.json" to MultipartFormFile(
                "knowledgeSpace.json",
                ContentType.OCTET_STREAM,
                knowledgeSpace.toJson().toString().byteInputStream()))
            .plus("someKnowledgeFileId" to MultipartFormFile(
                "someKnowledgeFileId.json",
                ContentType.OCTET_STREAM,
                knowledgeFile.toJson().toString().byteInputStream()
            ))
            .plus("query" to query)

        val actual = LLMSpacesQueryRequestBuilder.buildQueryRequest(knowledgeSpace, listOf(knowledgeFile), query)

        Assertions.assertEquals(actual.field("query"), expectedForm.field("query"))
        Assertions.assertEquals(String(actual.file("knowledgeSpace.json")!!.content.readAllBytes()), String(expectedForm.file("knowledgeSpace.json")!!.content.readAllBytes()))
        Assertions.assertEquals(String(actual.file("someKnowledgeFileId")!!.content.readAllBytes()), String(expectedForm.file("someKnowledgeFileId")!!.content.readAllBytes()))

    }

}