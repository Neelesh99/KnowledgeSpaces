package com.neelesh

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.server.Undertow
import org.http4k.server.asServer



fun main() {
    val printingApp: HttpHandler = PrintRequest().then(GPTUserApp(mongoOAuthPersistence))

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
