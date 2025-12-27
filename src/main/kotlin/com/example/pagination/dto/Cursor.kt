package com.example.pagination.dto

import java.time.Instant
import java.util.Base64

data class Cursor(
    val id: Long,
    val createdAt: Instant
) {
    fun encode(): String {
        val data = "$id:${createdAt.toEpochMilli()}"
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data.toByteArray())
    }

    companion object {
        fun decode(encoded: String): Cursor {
            val data = String(Base64.getUrlDecoder().decode(encoded))
            val parts = data.split(":")
            return Cursor(
                id = parts[0].toLong(),
                createdAt = Instant.ofEpochMilli(parts[1].toLong())
            )
        }
    }
}

