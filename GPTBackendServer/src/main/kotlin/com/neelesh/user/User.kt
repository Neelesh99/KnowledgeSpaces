package com.neelesh.user

import org.http4k.security.AccessToken

data class User(
    val username: String,
    val email: String,
    val cookieSwapString: String,
    val token: AccessToken
) {

}