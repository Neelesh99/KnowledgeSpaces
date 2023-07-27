package com.neelesh.llm

import com.neelesh.model.KnowledgeFile
import com.neelesh.model.KnowledgeSpace
import org.http4k.core.ContentType
import org.http4k.core.MultipartFormBody
import org.http4k.lens.MultipartFormFile

class LLMSpacesQueryRequestBuilder {

    companion object {
        fun buildQueryRequest(
            knowledgeSpace: KnowledgeSpace,
            knowledgeFiles: List<KnowledgeFile>,
            query: String
        ): MultipartFormBody {

            val formBody = MultipartFormBody()
                .plus("query" to query)
                .plus(
                    "knowledgeSpace.json" to MultipartFormFile(
                        "knowledgeSpace.json",
                        ContentType.OCTET_STREAM,
                        knowledgeSpace.toJson().toString().byteInputStream()
                    )
                )
            return knowledgeFiles.fold(formBody) { body, file ->
                body.plus(file.id to MultipartFormFile(
                    file.id + ".json",
                    ContentType.OCTET_STREAM,
                    file.toJson().toString().byteInputStream()
                ))
            }
        }
    }
}

