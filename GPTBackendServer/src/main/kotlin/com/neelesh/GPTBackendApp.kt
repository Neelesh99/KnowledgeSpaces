package com.neelesh

import com.neelesh.config.Dependencies
import com.neelesh.formats.JacksonMessage
import com.neelesh.formats.jacksonMessageLens
import com.neelesh.llm.IndexRequestHandler
import com.neelesh.llm.QueryRequestHandler
import com.neelesh.routes.ExampleContractRoute
import com.neelesh.routes.IndexRequestRoute
import com.neelesh.routes.QueryRequestRoute
import com.neelesh.security.InMemoryOAuthPersistence
import com.neelesh.security.InsecureTokenChecker
import com.neelesh.user.MongoBasedOAuthPersistence
import com.neelesh.user.User
import org.http4k.client.JavaHttpClient
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.security.OAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.google
import org.litote.kmongo.KMongo
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.time.Clock

// Google OAuth Example
// Browse to: http://localhost:9000/oauth - you'll be redirected to google for authentication

// this is a test implementation of the OAuthPersistence interface, which should be
// implemented by application developers
val inMemoryOAuthPersistence = InMemoryOAuthPersistence(Clock.systemUTC(), InsecureTokenChecker)

val mongoClient = KMongo.createClient("mongodb+srv://firehzb:$mongodbPass@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority")
val userCollection = mongoClient.getDatabase("myStuff").getCollection<User>("user")
val mongoOAuthPersistence = MongoBasedOAuthPersistence(userCollection, Clock.systemUTC(), InsecureTokenChecker)
// pre-defined configuration exist for common OAuth providers


private fun routingHttpHandler2() = "/docs" bind static(Classpath("META-INF/resources/webjars/swagger-ui/3.25.2"))

private fun routingHttpHandler(descriptionPath: String) = "docs" bind Method.GET to {
    Response(Status.FOUND).header("Location", "/docs/index.html?url=$descriptionPath")
}

private const val API_DESCRIPTION_PATH = "/contract/api/v1/swagger.json"

fun GPTUserApp(oAuthPersistence: OAuthPersistence, dependencies: Dependencies): HttpHandler {

    val oauthProvider = OAuthProvider.google(
        JavaHttpClient(),
        Credentials(googleClientId, googleClientSecret),
        Uri.of("http://localhost:9000/oauth/callback"),
        oAuthPersistence,
        scopes = listOf("openid", "email", "profile")
    )

    val indexRequestHandler = IndexRequestHandler(
        dependencies.blobStore,
        dependencies.knowledgeFileStore,
        dependencies.llmClient
    )

    val queryRequestHandler = QueryRequestHandler(
        dependencies.knowledgeFileStore,
        dependencies.llmClient
    )

    return routes(
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
            routes += IndexRequestRoute(indexRequestHandler)
            routes += QueryRequestRoute(queryRequestHandler)
        },

        routingHttpHandler(API_DESCRIPTION_PATH),

        routingHttpHandler2(),

        "/oauth" bind routes(
            "/" bind Method.GET to oauthProvider.authFilter.then {
                val cookie = it.cookie("securityServerAuth")
                val user = userCollection.findOne(User::cookieSwapString eq cookie!!.value)!!
                Response(Status.OK).body(user.toDtoJson().toPrettyString())
            },
            "/callback" bind Method.GET to oauthProvider.callback
        )
    )
}