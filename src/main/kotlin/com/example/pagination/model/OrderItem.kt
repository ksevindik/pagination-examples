package com.example.pagination.model

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    var productName: String = ""

    var quantity: Int = 1

    var price: BigDecimal = BigDecimal.ZERO

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order? = null
}

