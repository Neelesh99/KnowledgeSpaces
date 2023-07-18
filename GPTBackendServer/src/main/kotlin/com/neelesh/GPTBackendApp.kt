package com.neelesh

import com.neelesh.formats.JacksonMessage
import com.neelesh.formats.jacksonMessageLens
import com.neelesh.routes.ExampleContractRoute
import com.neelesh.security.InMemoryOAuthPersistence
import com.neelesh.security.InsecureTokenChecker
import org.http4k.client.JavaHttpClient
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.*
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.security.OAuthProvider
import org.http4k.security.google
import java.time.Clock

// Google OAuth Example
// Browse to: http://localhost:9000/oauth - you'll be redirected to google for authentication
val googleClientId = "myGoogleClientId"
val googleClientSecret = "myGoogleClientSecret"

// this is a test implementation of the OAuthPersistence interface, which should be
// implemented by application developers
val oAuthPersistence = InMemoryOAuthPersistence(Clock.systemUTC(), InsecureTokenChecker)

// pre-defined configuration exist for common OAuth providers
val oauthProvider = OAuthProvider.google(
    JavaHttpClient(),
    Credentials(googleClientId, googleClientSecret),
    Uri.of("http://localhost:9000/oauth/callback"),
    oAuthPersistence,
    scopes = listOf("openid", "email", "name")
)

private fun routingHttpHandler2() = "/docs" bind static(Classpath("META-INF/resources/webjars/swagger-ui/3.25.2"))

private fun routingHttpHandler(descriptionPath: String) = "docs" bind Method.GET to {
    Response(Status.FOUND).header("Location", "/docs/index.html?url=$descriptionPath")
}

private const val API_DESCRIPTION_PATH = "/contract/api/v1/swagger.json"

val GPTUserApp: HttpHandler = routes(
    "/ping" bind Method.GET to {
        Response(Status.OK).body("pong")
    },

    "/formats/json/jackson" bind Method.GET to {
        Response(Status.OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
    },

    "/contract/api/v1" bind contract {
        renderer = OpenApi3(ApiInfo("GPTBackendServer API", "v1.0"))

        // Return Swagger API definition under /contract/api/v1/swagger.json
        descriptionPath = "/swagger.json"

        // You can use security filter tio protect routes
        security = ApiKeySecurity(Query.int().required("api"), { it == 42 }) // Allow only requests with &api=42

        // Add contract routes
        routes += ExampleContractRoute()
    },

    routingHttpHandler(API_DESCRIPTION_PATH),

    routingHttpHandler2(),

    "/oauth" bind routes(
        "/" bind Method.GET to oauthProvider.authFilter.then {
            Response(Status.OK).body("hello!")
                                                             },
        "/callback" bind Method.GET to oauthProvider.callback
    )
)