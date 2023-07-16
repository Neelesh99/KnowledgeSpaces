package com.neelesh

import com.neelesh.formats.JacksonMessage
import com.neelesh.formats.jacksonMessageLens
import com.neelesh.routes.ExampleContractRoute
import org.http4k.client.JavaHttpClient
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.*
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.google

// Google OAuth Example
// Browse to: http://localhost:9000/oauth - you'll be redirected to google for authentication
val googleClientId = "myGoogleClientId"
val googleClientSecret = "myGoogleClientSecret"

// this is a test implementation of the OAuthPersistence interface, which should be
// implemented by application developers
val oAuthPersistence = InsecureCookieBasedOAuthPersistence("Google")

// pre-defined configuration exist for common OAuth providers
val oauthProvider = OAuthProvider.google(
    JavaHttpClient(),
    Credentials(googleClientId, googleClientSecret),
    Uri.of("http://localhost:9000/oauth/callback"),
    oAuthPersistence
)
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

    "/oauth" bind routes(
        "/" bind Method.GET to oauthProvider.authFilter.then { Response(Status.OK).body("hello!") },
        "/callback" bind Method.GET to oauthProvider.callback
    )
)