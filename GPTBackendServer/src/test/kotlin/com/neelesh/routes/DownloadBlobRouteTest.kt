package com.neelesh.routes

import arrow.core.right
import com.neelesh.model.BlobReference
import com.neelesh.model.DataType
import com.neelesh.storage.BlobHandler
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.*
import org.http4k.lens.MultipartFormFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DownloadBlobRouteTest {

    val blobHandler = mockk<BlobHandler>()
    val route = DownloadBlobRoute(blobHandler)

    @Test
    fun `will download blob as multipart form`() {
        val dataStream = "data".byteInputStream()
        val blobReference = BlobReference(
            "blobId",
            DataType.PLAIN_TEXT,
            "someFileName"
        )
        every { blobHandler.download(any()) } returns (blobReference to dataStream).right()

        val request = Request(Method.POST, "/download/blob").body("{\"blobId\":\"blobId\"}")
        val response = route(request)
//        Assertions.assertEquals(Status.OK, response.status)
        val formResponse = MultipartFormBody.from(response)
        Assertions.assertEquals("data", String(formResponse.file("someFileName")!!.content.readAllBytes()))
    }

}