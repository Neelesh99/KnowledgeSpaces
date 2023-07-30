package com.neelesh.routes

import com.neelesh.storage.BlobHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.MultipartFormFile

data class SimpleBlobDownloadRequest(
    val blobId: String
)

// the body lens here is imported as an extension function from the Jackson instance
val simpleBlobDownloadREquest = Body.auto<SimpleBlobDownloadRequest>().toLens()

object DownloadBlobRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/download/blob" meta {
        summary = "echoes the name and message sent to it"
        receiving(simpleBlobDownloadREquest to SimpleBlobDownloadRequest("someBlobId"))
        returning(Status.OK, simpleBlobDownloadREquest to SimpleBlobDownloadRequest("someBlobId"))
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(blobHandler: BlobHandler): HttpHandler = { request: Request ->
        val received = simpleBlobDownloadREquest(request)
        val result = blobHandler.download(received)
        result.fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                val form = MultipartFormBody()
                    .plus("filename" to it.first.fileName)
                    .plus(
                    it.first.fileName to MultipartFormFile(
                        it.first.fileName,
                        ContentType.OCTET_STREAM,
                        it.second
                    )
                )
                Response(Status.OK).body(form).header("Content-Type", ContentType.MultipartFormWithBoundary(form.boundary).toHeaderValue())
            }
        )
    }

    operator fun invoke(blobHandler: BlobHandler): ContractRoute = spec to echo(blobHandler)
}