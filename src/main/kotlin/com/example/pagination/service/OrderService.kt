package com.example.pagination.service

import com.example.pagination.dto.Cursor
import com.example.pagination.dto.CursorResponse
import com.example.pagination.dto.OrderResponse
import com.example.pagination.dto.OrderWithItemsResponse
import com.example.pagination.model.Order
import com.example.pagination.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {
    fun getOrdersAtPage(pageNumber:Int = 0, pageSize:Int = 10) : Page<Order> {
        val pageRequest = PageRequest.of(pageNumber,pageSize,
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        return orderRepository.findAll(pageRequest)
    }

    fun getOrdersWithItemsAtPage(pageNumber:Int = 0, pageSize:Int = 10) : Page<Order> {
        val pageRequest = PageRequest.of(pageNumber,pageSize,
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        return orderRepository.findOrdersWithItemsAtPage(pageRequest)
    }

    fun getOrdersAtSlice(pageNumber: Int = 0, pageSize: Int = 10): Slice<Order> {
        val pageRequest = PageRequest.of(pageNumber, pageSize,
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        return orderRepository.findBy(pageRequest)
    }

    fun getOrdersWithItemsAtSlice(pageNumber:Int = 0, pageSize:Int = 10) : Slice<Order> {
        val pageRequest = PageRequest.of(pageNumber,pageSize,
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        return orderRepository.findOrdersWithItemsAtSlice(pageRequest)
    }

    fun getOrdersAtCursor(encodedCursor: String?, pageSize: Int = 10): CursorResponse<OrderResponse> {
        val pageable = PageRequest.of(0, pageSize + 1)
        
        val orders = if (encodedCursor == null) {
            orderRepository.findOrdersFromStart(pageable)
        } else {
            val cursor = Cursor.decode(encodedCursor)
            orderRepository.findOrdersAfterCursor(cursor.createdAt, cursor.id, pageable)
        }

        val hasNext = orders.size > pageSize
        val content = orders.take(pageSize)
        val lastOrder = content.lastOrNull()
        val nextCursor = if (hasNext && lastOrder != null) {
            Cursor(lastOrder.id!!, lastOrder.createdAt!!).encode()
        } else null

        return CursorResponse.from(
            content = content.map(OrderResponse::from),
            size = pageSize,
            nextCursor = nextCursor,
            hasNext = hasNext
        )
    }

    fun getOrdersWithItemsAtCursor(encodedCursor: String?, pageSize: Int = 10): CursorResponse<OrderWithItemsResponse> {
        val pageable = PageRequest.of(0, pageSize + 1)

        val orderIds = if (encodedCursor == null) {
            orderRepository.findOrderIdsFromStart(pageable)
        } else {
            val cursor = Cursor.decode(encodedCursor)
            orderRepository.findOrderIdsAfterCursor(cursor.createdAt, cursor.id, pageable)
        }

        val orders = orderRepository.findOrdersWithItemsIn(orderIds)

        val hasNext = orders.size > pageSize
        val content = orders.take(pageSize)
        val lastOrder = content.lastOrNull()
        val nextCursor = if (hasNext && lastOrder != null) {
            Cursor(lastOrder.id!!, lastOrder.createdAt!!).encode()
        } else null

        return CursorResponse.from(
            content = content.map(OrderWithItemsResponse::from),
            size = pageSize,
            nextCursor = nextCursor,
            hasNext = hasNext
        )
    }
}