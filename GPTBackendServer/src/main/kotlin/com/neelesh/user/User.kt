package com.neelesh.user

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson
import org.http4k.security.AccessToken
import org.http4k.security.oauth.core.RefreshToken

data class User(
    val username: String,
    val email: String,
    val cookieSwapString: String,
    val token: BasicAccessToken
) {
    fun toDtoJson() : JsonNode {
        return Jackson.obj(
            "username" to Jackson.string(username),
            "email" to Jackson.string(email)
        )
    }
}

data class BasicAccessToken(
    override val value: String,
    override val type: String?,
    override val expiresIn: Long?,
    override val scope: String?,
    override val refreshToken: RefreshToken?
) : AccessToken {

    companion object {
        fun fromAccessToken(accessToken: AccessToken) : BasicAccessToken {
            return BasicAccessToken(
                accessToken.value,
                accessToken.type,
                accessToken.expiresIn,
                accessToken.scope,
                accessToken.refreshToken
            )
        }
    }
}
