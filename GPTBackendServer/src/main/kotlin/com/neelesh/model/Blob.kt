package com.neelesh.model

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.contract.openapi.OpenAPIJackson.string
import org.http4k.format.Jackson
import java.net.URL

data class Blob(
    val id: String,
    val name: String,
    val link: String
) {

    fun toJson() : JsonNode {
        return Jackson.obj(
            "id" to string(id),
            "name" to string(name),
            "link" to string(link)
        )
    }

    companion object {

        fun fromJson(jsonNode: JsonNode) : Blob {
            return Blob(
                jsonNode.get("id").textValue(),
                jsonNode.get("name").textValue(),
                jsonNode.get("link").textValue()
            )
        }

    }
}
