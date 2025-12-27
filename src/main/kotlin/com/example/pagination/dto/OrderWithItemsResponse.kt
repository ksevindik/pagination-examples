package com.example.pagination.dto

import com.example.pagination.model.Order
import java.time.Instant

data class OrderWithItemsResponse(
    val id: Long,
    val status: String,
    val createdAt: Instant?,
    val itemsCount: Int
) {
    companion object {
        fun from(order: Order): OrderWithItemsResponse {
            return OrderWithItemsResponse(
                id = order.id!!,
                status = order.status,
                createdAt = order.createdAt,
                itemsCount = order.items.size
            )
        }
    }
}

