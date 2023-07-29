package com.neelesh.util

import java.util.UUID

class UUIDGenerator {

    fun get(): String {
        return UUID.randomUUID().toString()
    }

}