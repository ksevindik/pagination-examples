package com.example.pagination.dto

import com.example.pagination.model.Order
import java.time.Instant

data class OrderResponse(
    val id: Long,
    val status: String,
    val createdAt: Instant?
) {
    companion object {
        fun from(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id!!,
                status = order.status,
                createdAt = order.createdAt
            )
        }
    }
}

