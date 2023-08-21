package com.neelesh.routes

import arrow.core.flatMap
import com.neelesh.llm.IndexRequestHandler
import com.neelesh.model.DataType
import com.neelesh.storage.BlobHandler
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import java.io.InputStream

data class SimpleBlobUploadRequest(
    val type: DataType,
    val fileName: String,
    val dataStream: InputStream,
    val knowledgeFileTarget: String,
    val email: String
)

// the body lens here is imported as an extension function from the Jackson instance
val simpleBlobUploadRequest = Body.auto<SimpleBlobUploadRequest>().toLens()

object UploadBlobRoute {
    // this specifies the route contract, including examples of the input and output body objects - they will
    // get exploded into JSON schema in the OpenAPI docs
    private val spec = "/upload/blob" meta {
    } bindContract Method.POST

    // note that because we don't have any dynamic parameters, we can use a HttpHandler instance instead of a function
    private fun echo(blobHandler: BlobHandler, indexRequestHandler: IndexRequestHandler): HttpHandler = { request: Request ->
        val received = MultipartFormBody.from(request)
        val uploadRequest = SimpleBlobUploadRequest(
            DataType.valueOf(received.field("dataType")!!.value),
            received.field("fileName")!!.value,
            received.file("file")!!.content,
            received.field("knowledgeFileTarget")!!.value,
            received.field("email")!!.value
        )
        val indexRequest = SimpleIndexRequest(
            uploadRequest.email,
            uploadRequest.knowledgeFileTarget
        )
        val result = blobHandler.upload(uploadRequest)
        result
            .flatMap { indexRequestHandler.handle(indexRequest) }
            .fold(
            {
                Response(Status.INTERNAL_SERVER_ERROR).body(it.message ?: "Internal Server Error")
            }, {
                Response(Status.OK).body(it)
            }
        )
    }

    operator fun invoke(blobHandler: BlobHandler, indexRequestHandler: IndexRequestHandler): ContractRoute = spec to echo(blobHandler, indexRequestHandler)
}