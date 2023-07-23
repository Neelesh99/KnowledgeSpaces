package com.neelesh

import com.neelesh.config.Dependencies
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.client.OkHttp



fun main() {
    val client: HttpHandler = OkHttp()
    val dependencies = Dependencies(client)
    val printingApp: HttpHandler = PrintRequest().then(GPTUserApp(mongoOAuthPersistence, dependencies))

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
