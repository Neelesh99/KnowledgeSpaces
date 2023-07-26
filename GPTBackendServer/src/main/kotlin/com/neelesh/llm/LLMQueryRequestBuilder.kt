package com.neelesh.llm

import com.neelesh.model.KnowledgeFile
import org.http4k.core.ContentType
import org.http4k.core.MultipartFormBody
import org.http4k.lens.MultipartFormFile

class LLMQueryRequestBuilder {

    companion object {
        fun buildQueryRequest(
            knowledgeFile: KnowledgeFile,
            query: String
        ): MultipartFormBody {

            return MultipartFormBody()
                .plus("query" to query)
                .plus(
                    "knowledgeFile.json" to MultipartFormFile(
                        "knowledgeFile.json",
                        ContentType.OCTET_STREAM,
                        knowledgeFile.toJson().toString().byteInputStream()
                    )
                )
            }
        }
    }

