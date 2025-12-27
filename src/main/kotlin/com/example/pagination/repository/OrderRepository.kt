package com.example.pagination.repository

import com.example.pagination.model.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<Order, Long> {
    @Query("""
        SELECT DISTINCT o FROM Order o 
        LEFT JOIN FETCH o.items 
    """)
    fun findOrdersWithItemsAtPage(pageable: Pageable) : Page<Order>

    fun findBy(pageable: Pageable): Slice<Order>

    @Query("""
        SELECT DISTINCT o FROM Order o 
        LEFT JOIN FETCH o.items 
    """)
    fun findOrdersWithItemsAtSlice(pageable: Pageable) : Slice<Order>


    @Query("""
        SELECT o FROM Order o 
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    fun findOrdersFromStart(pageable: Pageable): List<Order>

    @Query("""
        SELECT o FROM Order o 
        WHERE o.createdAt < :createdAt 
           OR (o.createdAt = :createdAt AND o.id < :id) 
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    fun findOrdersAfterCursor(createdAt: java.time.Instant, id: Long, pageable: Pageable): List<Order>

    @Query("""
        SELECT o.id FROM Order o 
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    fun findOrderIdsFromStart(pageable: Pageable): List<Long>

    @Query("""
        SELECT o.id FROM Order o 
        WHERE o.createdAt < :createdAt 
           OR (o.createdAt = :createdAt AND o.id < :id) 
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    fun findOrderIdsAfterCursor(createdAt: java.time.Instant, id: Long, pageable: Pageable): List<Long>

    @Query("""
        SELECT DISTINCT o FROM Order o 
        LEFT JOIN FETCH o.items
        WHERE o.id in :ids
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    fun findOrdersWithItemsIn(ids: List<Long>) : List<Order>
}