package com.neelesh.acceptance.LLMServer

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes

fun LLMApp() : HttpHandler {
    return routes(
        "/ping" bind Method.GET to {
            Response(Status.OK).body("pong")
        },
        "/query" bind Method.GET to {
            Response(Status.OK).body("find")
        }
    )
}