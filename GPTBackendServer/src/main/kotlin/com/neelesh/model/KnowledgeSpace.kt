package com.neelesh.model

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.contract.openapi.OpenAPIJackson.array
import org.http4k.contract.openapi.OpenAPIJackson.string
import org.http4k.format.Jackson

data class KnowledgeSpace(
    val id: String,
    val name: String,
    val email: String,
    val files: List<String>
) {

    fun toJson() : JsonNode {
        return Jackson.obj(
            "id" to string(id),
            "name" to string(name),
            "email" to string(email),
            "files" to array(files.map { string(it) })
        )
    }

    companion object {

        fun fromJson(jsonNode: JsonNode) : KnowledgeSpace {
            return KnowledgeSpace(
                jsonNode.get("id").textValue(),
                jsonNode.get("name").textValue(),
                jsonNode.get("email").textValue(),
                jsonNode.get("files").map { it.textValue() }
            )
        }

    }

}