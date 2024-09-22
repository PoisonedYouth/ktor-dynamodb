package com.poisonedyouth.order

import com.poisonedyouth.product.ProductId

class OrderService(
    private val orderRepository: OrderRepository
) {

    suspend fun addOrder(order: Order) {
        val existingOrder = orderRepository.findById(orderId = order.orderId.value, productId = order.productId.value)
        if (existingOrder != null) {
            error("Order with id ${order.orderId} and productId '${order.productId}' already exists.")
        }
        orderRepository.add(order)
    }

    suspend fun updateOrder(order: Order) {
        orderRepository.findById(orderId = order.orderId.value, productId = order.productId.value)
            ?: error("Order with id ${order.orderId} and productId '${order.productId}' already exists.")
        orderRepository.update(order)
    }

    suspend fun getOrder(orderId: OrderId, productId: ProductId): Order? {
        return orderRepository.findById(orderId = orderId.value, productId = productId.value)
    }

    suspend fun deleteOrder(orderId: OrderId, productId: ProductId) {
        orderRepository.deleteById(orderId = orderId.value, productId = productId.value)
    }

    suspend fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }
}