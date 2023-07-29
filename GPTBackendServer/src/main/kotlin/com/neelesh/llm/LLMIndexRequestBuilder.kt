package com.neelesh.llm

import com.neelesh.model.BlobReference
import com.neelesh.model.IndexRequest
import com.neelesh.model.UserDetails
import org.http4k.core.ContentType
import org.http4k.core.MultipartFormBody
import org.http4k.lens.MultipartFormFile
import java.io.InputStream

class LLMIndexRequestBuilder {

    companion object {
        fun buildIndexRequest(
            email: String,
            knowledgeFileTarget: String,
            blobs: List<Pair<BlobReference, InputStream>>
        ) : MultipartFormBody {
            val indexRequest = IndexRequest(
                UserDetails(email),
                knowledgeFileTarget,
                blobs.map { it.first }
            )

            val listOfFiles = blobs.map { (reference, dataStream) ->
                MultipartFormFile(reference.fileName, ContentType.OCTET_STREAM, dataStream)
            }

            val formBody =  MultipartFormBody()
                .plus("indexRequestFileName" to "indexRequest")
                .plus("indexRequest" to MultipartFormFile(
                    "indexRequest.json",
                    ContentType.OCTET_STREAM,
                    indexRequest.toJson().toString().byteInputStream()
                ))
            return listOfFiles.fold(formBody) { body, file -> body.plus(file.filename to file) }
        }
    }

}