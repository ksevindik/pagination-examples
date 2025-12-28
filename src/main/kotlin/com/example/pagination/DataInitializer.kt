package com.example.pagination

import com.example.pagination.model.Order
import com.example.pagination.model.OrderItem
import com.example.pagination.repository.OrderRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlin.random.nextLong

@Component
class DataInitializer(
    private val orderRepository: OrderRepository
) : CommandLineRunner {

    private val products = listOf(
        "Laptop" to BigDecimal("999.99"),
        "Smartphone" to BigDecimal("699.99"),
        "Headphones" to BigDecimal("149.99"),
        "Keyboard" to BigDecimal("79.99"),
        "Mouse" to BigDecimal("49.99"),
        "Monitor" to BigDecimal("349.99"),
        "Webcam" to BigDecimal("89.99"),
        "USB Cable" to BigDecimal("12.99"),
        "Power Bank" to BigDecimal("39.99"),
        "Tablet" to BigDecimal("449.99")
    )

    override fun run(vararg args: String) {
        if (orderRepository.count() == 0L) {
            val statuses = listOf("CREATED", "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED")
            val orders = (1..98).map { index ->
                Order().apply {
                    status = statuses[index % statuses.size]
                    createdAt = Instant.ofEpochSecond(Random.nextLong(LongRange(1,10_000)))
                    
                    // Add 1-4 random items to each order
                    val itemCount = Random.nextInt(1, 5)
                    val selectedProducts = products.shuffled().take(itemCount)
                    selectedProducts.forEach { (name, price) ->
                        val item = OrderItem().apply {
                            productName = name
                            quantity = Random.nextInt(1, 4)
                            this.price = price
                        }
                        addItem(item)
                    }
                }
            }
            orderRepository.saveAll(orders)
            println("Initialized database with 98 orders and their items")
        }
    }
}

