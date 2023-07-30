package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.MultipartFormBody
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DownloadBlobAcceptanceTest : BaseAcceptanceTest() {
    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList(), emptyList(), emptyList())
        val server = setupClient(stubLlmApp, 0)
        val expectedReference = BlobReference(
            "someBlobId",
            DataType.PLAIN_TEXT,
            "someFileName"
        )
        val blobData = "someData".byteInputStream()
        blobStore.storeBlob(expectedReference, blobData)
        val body = "{\"blobId\":\"someBlobId\"}"
        val request = Request(Method.POST, "http://localhost:${server.port()}/contract/api/v1/download/blob?api=42")
            .body(body)
        val testClient = OkHttp()
        val response = testClient(request)
        val received = MultipartFormBody.from(response)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someData", String(received.file("someFileName")!!.content.readAllBytes()))
    }

}