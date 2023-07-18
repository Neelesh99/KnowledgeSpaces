package com.neelesh.user

import com.mongodb.client.MongoCollection
import com.neelesh.security.TokenChecker
import io.mockk.mockk
import java.time.Clock

class MongoBasedOAuthPersistenceTest {

    val mongoUserCollection = mockk<MongoCollection<User>>()
    val clock = mockk<Clock>()
    val tokenChecker = mockk<TokenChecker>()

    val mongoBasedOAuthPersistence = MongoBasedOAuthPersistence(
        mongoUserCollection,
        clock,
        tokenChecker
    )


}