package com.neelesh.user

import com.mongodb.client.MongoCollection
import com.neelesh.security.TokenChecker
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.invalidateCookie
import org.http4k.format.Jackson
import org.http4k.security.*
import org.http4k.security.openid.IdToken
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.nio.charset.Charset
import java.time.Clock
import java.time.Duration
import java.util.*

class MongoBasedOAuthPersistence(
    val mongoUserCollection: MongoCollection<User>,
    val clock: Clock,
    val tokenChecker: TokenChecker
) : OAuthPersistence {
    private val csrfName = "securityServerCsrf"
    private val originalUriName = "securityServerUri"
    private val clientAuthCookie = "securityServerAuth"

    override fun retrieveCsrf(request: Request) = request.cookie(csrfName)?.value?.let(::CrossSiteRequestForgeryToken)

    override fun retrieveNonce(request: Request): Nonce? = null
    override fun retrieveOriginalUri(request: Request): Uri? = request.cookie(originalUriName)?.value?.let(Uri::of)

    override fun retrieveToken(request: Request) = (tryBearerToken(request)
        ?: tryCookieToken(request))
        ?.takeIf(tokenChecker::check)

    override fun assignCsrf(redirect: Response, csrf: CrossSiteRequestForgeryToken) = redirect.cookie(expiring(csrfName, csrf.value))

    override fun assignNonce(redirect: Response, nonce: Nonce): Response = redirect

    override fun assignOriginalUri(redirect: Response, originalUri: Uri): Response = redirect.cookie(expiring(originalUriName, originalUri.toString()))

    override fun assignToken(request: Request, redirect: Response, accessToken: AccessToken, idToken: IdToken?) =
        idToken?.let { idTokenRealised ->
            UUID.randomUUID().let {
                val jsonString = Base64.getDecoder().decode(idTokenRealised.value.split(".").get(1)).toString(Charset.defaultCharset())
                val json = Jackson.parse(jsonString)
                val ifFoundUser = mongoUserCollection.findOne(User::username eq json.get("name").textValue())
                if (ifFoundUser is User) {
                    val editedUser = ifFoundUser.copy(cookieSwapString = it.toString(), token = BasicAccessToken.fromAccessToken(accessToken))
                    mongoUserCollection.replaceOne(User::username eq json.get("name").textValue(), editedUser)
                } else {
                    mongoUserCollection.insertOne( User(
                        json.get("name").textValue(),
                        json.get("email").textValue(),
                        it.toString(),
                        BasicAccessToken.fromAccessToken(accessToken)
                    ))
                }
                redirect
                    .cookie(expiring(clientAuthCookie, it.toString()))
                    .invalidateCookie(csrfName)
                    .invalidateCookie(originalUriName)
            }
        } ?: redirect

    override fun authFailureResponse(reason: OAuthCallbackError) = Response(Status.FORBIDDEN)
        .invalidateCookie(csrfName)
        .invalidateCookie(originalUriName)
        .invalidateCookie(clientAuthCookie)

    private fun tryCookieToken(request: Request) =
        request.cookie(clientAuthCookie)?.value?.let { cookieString -> mongoUserCollection.findOne(User::cookieSwapString eq cookieString)?.token }

    private fun tryBearerToken(request: Request) = request.header("Authorization")
        ?.removePrefix("Bearer ")
        ?.let { AccessToken(it) }

    private fun expiring(name: String, value: String) = Cookie(name, value,
        path = "/",
        expires = clock.instant().plus(Duration.ofDays(1)))

}
