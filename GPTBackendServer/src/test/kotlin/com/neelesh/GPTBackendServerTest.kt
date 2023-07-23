package com.neelesh

import com.neelesh.config.Dependencies
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GPTBackendServerTest {

    @Test
    fun `Ping test`() {
        assertEquals(
            Response(OK).body("pong"),
            GPTUserApp(InsecureCookieBasedOAuthPersistence("cookie"), Dependencies(OkHttp()))(Request(GET, "/ping")))
    }

}
