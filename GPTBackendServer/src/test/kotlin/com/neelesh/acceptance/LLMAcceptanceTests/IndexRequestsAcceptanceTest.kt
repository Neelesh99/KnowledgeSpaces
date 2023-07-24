package com.neelesh.acceptance.LLMAcceptanceTests

import com.neelesh.GPTUserApp
import com.neelesh.acceptance.Stubs.StubLLMApp
import com.neelesh.config.Dependencies
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IndexRequestsAcceptanceTest {

    @Test
    fun `will post index request to server and it will be sent to llm for indexing`() {
        val stubLlmApp = StubLLMApp(emptyList())
        val server = setupClient(stubLlmApp, 0)

        val request = Request(Method.POST, "http://localhost:${server.port()}/api/v1/sendRequest")
            .body("{\"email\":\"someEmail\",\"knowledgeFileTarget\":\"someKnowledgeFileId\"}")

        val testClient = OkHttp()
        val response = testClient(request)
        Assertions.assertEquals(Status.OK, response.status)
    }

    fun setupClient(stubLlmApp: StubLLMApp, port: Int): Http4kServer {
        val server = GPTUserApp(
            InsecureCookieBasedOAuthPersistence("someThing"),
            Dependencies(stubLlmApp.server()))
        return server.asServer(Undertow(port = port)).start()
    }

}