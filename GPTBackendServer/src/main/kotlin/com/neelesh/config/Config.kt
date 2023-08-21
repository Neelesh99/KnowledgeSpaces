package com.neelesh.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int

val googleClientIdLens = EnvironmentKey.defaulted("GOOGLE_CLIENT_ID", "someId")
val googleClientSecretLens = EnvironmentKey.defaulted("GOOGLE_CLIENT_SECRET", "someSecret")
val mongoDBPasswordLens = EnvironmentKey.defaulted("MONGO_DB_PASSWORD", "unknown")
val mongoDBUsernameLens = EnvironmentKey.defaulted("MONGO_DB_USERNAME", "unknown")
val googleProjectIdLens = EnvironmentKey.defaulted("GOOGLE_PROJECT_ID", "unknown")
val callbackLocationLens = EnvironmentKey.defaulted("CALLBACK_LOCATION", "http://localhost:9000/oauth/callback")
val crossOriginLocationLens = EnvironmentKey.defaulted("CROSS_ORIGIN_LOCATION", "http://localhost:5173")
val llmServerSchemeLens = EnvironmentKey.defaulted("LLM_SERVER_SCHEME", "http")
val llmServerHostLens = EnvironmentKey.defaulted("LLM_SERVER_HOST", "localhost")
val llmServerPortLens = EnvironmentKey.int().defaulted("LLM_SERVER_PORT", 2323)

data class Config(
    val googleClientId: String,
    val googleClientSecret: String,
    val mongoDBPassword: String,
    val mongoDBUsername: String,
    val googleProjectId: String,
    val callbackLocation: String,
    val crossOriginLocation: String,
    val llmServerScheme: String,
    val llmServerHost: String,
    val llmServerPort: Int
) {

    companion object {

        val DEFAULT = fromEnvironment(Environment.ENV)

        fun fromEnvironment(environment: Environment) : Config {
            return Config(
                googleClientIdLens(environment),
                googleClientSecretLens(environment),
                mongoDBPasswordLens(environment),
                mongoDBUsernameLens(environment),
                googleProjectIdLens(environment),
                callbackLocationLens(environment),
                crossOriginLocationLens(environment),
                llmServerSchemeLens(environment),
                llmServerHostLens(environment),
                llmServerPortLens(environment)
            )
        }
    }

}