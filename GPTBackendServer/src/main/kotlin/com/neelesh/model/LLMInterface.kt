package com.neelesh.model

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.contract.openapi.OpenAPIJackson.array
import org.http4k.contract.openapi.OpenAPIJackson.string
import org.http4k.format.Jackson

interface Request {
    fun toJson() : JsonNode
}

enum class DataType {
    PDF_DOCUMENT,
    WEB_LINK,
    YOUTUBE_LINK,
    PLAIN_TEXT
}

data class BlobReference (
    val blobId: String,
    val type: DataType,
    val fileName: String) {

    fun toJson() : JsonNode {

        return Jackson.obj(
            "blobId" to string(blobId),
            "type" to string(type.name),
            "fileName" to string(fileName)
        )
    }

    companion object {
        fun fromJson(jsonNode: JsonNode) : BlobReference {
            return BlobReference(
                jsonNode.get("blobId").textValue(),
                DataType.valueOf(jsonNode.get("type").textValue()),
                jsonNode.get("fileName").textValue()
            )
        }
    }
}

data class UserDetails (
    val email: String
) {
    fun toJson() : JsonNode {
        return Jackson.obj(
            "email" to string(email)
        )
    }

    companion object {
        fun fromJson(jsonNode: JsonNode) : UserDetails {
            return UserDetails(
                jsonNode.get("email").textValue()
            )
        }
    }
}

data class IndexRequest (
    val userDetails: UserDetails,
    val knowledgeFileTarget: String,
    val blobReferences: List<BlobReference>
    ) : Request {

    override fun toJson(): JsonNode {
        return Jackson.obj(
            "userDetails" to userDetails.toJson(),
            "knowledgeFileTarget" to string(knowledgeFileTarget),
            "blobReferences" to array(blobReferences.map { it.toJson() })
        )
    }

    companion object {
        fun fromJson(jsonNode: JsonNode) : IndexRequest {
            return IndexRequest(
                UserDetails.fromJson(jsonNode.get("userDetails")),
                jsonNode.get("knowledgeFileTarget").textValue(),
                jsonNode.get("blobReferences").map { BlobReference.fromJson(it) }
            )
        }
    }
}