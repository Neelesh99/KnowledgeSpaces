package com.neelesh.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey

val googleClientIdLens = EnvironmentKey.defaulted("GOOGLE_CLIENT_ID", "someId")
val googleClientSecretLens = EnvironmentKey.defaulted("GOOGLE_CLIENT_SECRET", "someSecret")
val mongoDBPasswordLens = EnvironmentKey.defaulted("MONGO_DB_PASSWORD", "unknown")
val mongoDBUsernameLens = EnvironmentKey.defaulted("MONGO_DB_USERNAME", "unknown")

data class Config(
    val googleClientId: String,
    val googleClientSecret: String,
    val mongoDBPassword: String,
    val mongoDBUsername: String
) {

    companion object {

        val DEFAULT = fromEnvironment(Environment.ENV)

        fun fromEnvironment(environment: Environment) : Config {
            return Config(
                googleClientIdLens(environment),
                googleClientSecretLens(environment),
                mongoDBPasswordLens(environment),
                mongoDBUsernameLens(environment)
            )
        }
    }

}