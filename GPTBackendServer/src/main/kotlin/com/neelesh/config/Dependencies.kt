package com.neelesh.config

import org.http4k.core.HttpHandler

data class Dependencies(
    val llmClient: HttpHandler
)