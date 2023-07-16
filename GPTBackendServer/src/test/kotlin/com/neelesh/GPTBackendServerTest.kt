package com.neelesh

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GPTBackendServerTest {

    @Test
    fun `Ping test`() {
        assertEquals(Response(OK).body("pong"), GPTUserApp(Request(GET, "/ping")))
    }

}
