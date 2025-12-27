package com.example.pagination.controller

import com.example.pagination.dto.CursorResponse
import com.example.pagination.dto.OrderResponse
import com.example.pagination.dto.OrderWithItemsResponse
import com.example.pagination.dto.SliceResponse
import com.example.pagination.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {
    @GetMapping("/atPage")
    fun getOrdersAtPage(@RequestParam pageNumber:Int=0, @RequestParam pageSize:Int=10) : Page<OrderResponse> {
        return orderService.getOrdersAtPage(pageNumber, pageSize).map(OrderResponse::from)
    }

    @GetMapping("/atPageWithItems")
    fun getOrdersWithItemsAtPage(@RequestParam pageNumber:Int=0, @RequestParam pageSize:Int=10) : Page<OrderWithItemsResponse> {
        return orderService.getOrdersWithItemsAtPage(pageNumber, pageSize).map(OrderWithItemsResponse::from)
    }

    @GetMapping("/atSlice")
    fun getOrdersAtSlice(@RequestParam pageNumber: Int = 0, @RequestParam pageSize: Int = 10): SliceResponse<OrderResponse> {
        val slice = orderService.getOrdersAtSlice(pageNumber, pageSize).map(OrderResponse::from)
        return SliceResponse.from(slice)
    }

    @GetMapping("/atSliceWithItems")
    fun getOrdersWithItemsAtSlice(@RequestParam pageNumber: Int = 0, @RequestParam pageSize: Int = 10): SliceResponse<OrderWithItemsResponse> {
        val slice = orderService.getOrdersWithItemsAtSlice(pageNumber, pageSize).map(OrderWithItemsResponse::from)
        return SliceResponse.from(slice)
    }

    @GetMapping("/atCursor")
    fun getOrdersAtCursor(
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): CursorResponse<OrderResponse> {
        return orderService.getOrdersAtCursor(cursor, pageSize)
    }

    @GetMapping("/atCursorWithItems")
    fun getOrdersWithItemsAtCursor(
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): CursorResponse<OrderWithItemsResponse> {
        return orderService.getOrdersWithItemsAtCursor(cursor, pageSize)
    }
}