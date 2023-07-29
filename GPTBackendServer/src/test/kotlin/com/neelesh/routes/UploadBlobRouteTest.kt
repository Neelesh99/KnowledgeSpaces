package com.neelesh.routes

import arrow.core.right
import com.neelesh.model.DataType
import com.neelesh.storage.BlobHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.*
import org.http4k.lens.MultipartForm
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UploadBlobRouteTest {

    val blobHandler = mockk<BlobHandler>()
    val route = UploadBlobRoute(blobHandler)

    @Test
    fun `will upload blob as multipart form`() {
        val dataStream = "data".byteInputStream()
        val simpleBlobUploadRequest = SimpleBlobUploadRequest(
            DataType.PLAIN_TEXT,
            "someFileName",
            dataStream,
            "someKnowledgeFileId",
            "someEmail"
        )
        every { blobHandler.handle(any()) } returns "someKnowledgeFileId".right()

        val inputForm = MultipartFormBody().plus("dataType" to "PLAIN_TEXT")
            .plus("fileName" to "someFileName")
            .plus("knowledgeFileTarget" to "someKnowledgeFileId")
            .plus("email" to "someEmail")
            .plus("file" to MultipartFormFile(
                "someFileName",
                ContentType.OCTET_STREAM,
                dataStream
            ))

        val request = Request(Method.POST, "/upload/blob").body(inputForm).header("Content-Type", ContentType.MultipartFormWithBoundary(inputForm.boundary).toHeaderValue())
        val response = route(request)
        Assertions.assertEquals(Status.OK, response.status)
        Assertions.assertEquals("someKnowledgeFileId", response.bodyString())
    }

}