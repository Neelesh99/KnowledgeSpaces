package com.neelesh.model

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.contract.openapi.OpenAPIJackson.string
import org.http4k.format.Jackson
import org.http4k.format.Jackson.array

data class KnowledgeFile(
    val id: String,
    val email: String,
    val name: String,
    val blobIds: List<String>,
    val indexDict: String
) {
    fun toJson() : JsonNode {
        return Jackson.obj(
            "id" to string(id),
            "email" to string(email),
            "name" to string(name),
            "blobIds" to array(blobIds.map { string(it) }),
            "indexDict" to string(indexDict)
        )
    }

    companion object {

        fun fromJson(jsonNode: JsonNode) : KnowledgeFile {
            return KnowledgeFile(
                jsonNode.get("id").textValue(),
                jsonNode.get("email").textValue(),
                jsonNode.get("name").textValue(),
                jsonNode.get("blobIds").map { it.textValue() },
                jsonNode.get("indexDict").textValue()
            )
        }

    }
}
