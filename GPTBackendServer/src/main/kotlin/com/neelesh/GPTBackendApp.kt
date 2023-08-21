package com.neelesh

import com.neelesh.config.Config
import com.neelesh.config.Dependencies
import com.neelesh.formats.JacksonMessage
import com.neelesh.formats.jacksonMessageLens
import com.neelesh.llm.IndexRequestHandler
import com.neelesh.llm.QueryRequestHandler
import com.neelesh.llm.SpacesQueryRequestHandler
import com.neelesh.persistence.KnowledgeFileHandler
import com.neelesh.persistence.KnowledgeSpaceHandler
import com.neelesh.routes.*
import com.neelesh.security.InMemoryOAuthPersistence
import com.neelesh.security.InsecureTokenChecker
import com.neelesh.storage.BlobHandler
import com.neelesh.user.User
import com.neelesh.util.UUIDGenerator
import org.http4k.client.JavaHttpClient
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.filter.AnyOf
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters
import org.http4k.lens.Query
import org.http4k.lens.string
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


// pre-defined configuration exist for common OAuth providers


private fun routingHttpHandler2() = "/docs" bind static(Classpath("META-INF/resources/webjars/swagger-ui/3.25.2"))

private fun routingHttpHandler(descriptionPath: String) = "docs" bind Method.GET to {
    Response(Status.FOUND).header("Location", "/docs/index.html?url=$descriptionPath")
}

private const val API_DESCRIPTION_PATH = "/contract/api/v1/swagger.json"

class AttachReferrerFilter(val authFilter: Filter) {
    fun invoke(request: Request): Response {
        val requestLink = request.uri.query.split("=")[1]
        val then = authFilter.then {
            Response(Status.TEMPORARY_REDIRECT)
                .header("Location", requestLink)
        }
        return then(request)
    }

}

fun GPTUserApp(oAuthPersistence: OAuthPersistence, dependencies: Dependencies, config: Config): HttpHandler {

    val mongoClient = KMongo.createClient("mongodb+srv://firehzb:${config.mongoDBPassword}@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority")
    val userCollection = mongoClient.getDatabase("myStuff").getCollection<User>("user")

    val oauthProvider = OAuthProvider.google(
        JavaHttpClient(),
        Credentials(config.googleClientId, config.googleClientSecret),
        Uri.of(config.callbackLocation),
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

    val spaceQueryRequestHandler = SpacesQueryRequestHandler(
        dependencies.knowledgeFileStore,
        dependencies.knowledgeSpaceStore,
        dependencies.llmClient
    )

    val blobHandler = BlobHandler(
        dependencies.knowledgeFileStore,
        dependencies.blobStore,
        UUIDGenerator()
    )

    val knowledgeFileHandler = KnowledgeFileHandler(
        dependencies.knowledgeFileStore,
        UUIDGenerator()
    )

    val knowledgeSpaceHandler = KnowledgeSpaceHandler(
        dependencies.knowledgeSpaceStore,
        UUIDGenerator()
    )

    val corsPolicy = CorsPolicy(
        originPolicy = OriginPolicy.AnyOf(config.crossOriginLocation ), // TODO Replace with the appropriate client origin(s)
        headers = listOf("Content-Type", "Authorization"), // TODO Consider adding back Authorization
        methods = listOf(Method.GET, Method.POST /*, Method.PUT, Method.DELETE */) // TODO Double Check Completeness
    )

    val corsMiddleware = ServerFilters.Cors(corsPolicy)

    return corsMiddleware.then(routes(
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
            security = ApiKeySecurity(Query.string().required("api"), { it == "42" }) // Allow only requests with &api=42

            // Add contract routes
            routes += ExampleContractRoute()
            routes += IndexRequestRoute(indexRequestHandler)
            routes += QueryRequestRoute(queryRequestHandler)
            routes += SpaceQueryRequestRoute(spaceQueryRequestHandler)
            routes += UploadBlobRoute(blobHandler, indexRequestHandler)
            routes += DownloadBlobRoute(blobHandler)
            routes += CreateKnowledgeFileRoute(knowledgeFileHandler)
            routes += UpdateKnowledgeFileRoute(knowledgeFileHandler)
            routes += CreateKnowledgeSpaceRoute(knowledgeSpaceHandler)
            routes += UpdateKnowledgeSpaceRoute(knowledgeSpaceHandler)
            routes += GetFilesRoute(knowledgeFileHandler)
        },

        routingHttpHandler(API_DESCRIPTION_PATH),

        routingHttpHandler2(),

        "/oauth" bind routes(
            "/" bind Method.GET to oauthProvider.authFilter.then {
                Response(Status.TEMPORARY_REDIRECT)
                    .header("Location", it.header("Referer"))
            },
            "/sd" bind Method.GET to {
                AttachReferrerFilter(oauthProvider.authFilter).invoke(it)
            },
            "/getUser" bind Method.GET to {
                val cookie = it.cookie("securityServerAuth")
                val user = userCollection.findOne(User::cookieSwapString eq cookie!!.value)!!
                Response(Status.OK)
                    .header("Access-Control-Allow-Origin", config.crossOriginLocation)
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET")
                    .body(user.toDtoJson().toString())
            },
            "/callback" bind Method.GET to oauthProvider.callback
        )

    ))
}