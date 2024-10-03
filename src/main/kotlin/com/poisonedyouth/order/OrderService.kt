package com.poisonedyouth.order

import com.poisonedyouth.product.ProductId
import java.time.Instant
import java.time.temporal.ChronoUnit

class OrderService(
    private val orderRepository: OrderRepository
) {

    suspend fun addOrder(order: Order) {
        val existingOrder = orderRepository.findById(customerId = order.customerId.value, orderDate = order.orderDate.toEpochMilli())
        if (existingOrder != null) {
            error("Order with customerId ${order.customerId} and orderDate '${order.orderDate}' already exists.")
        }
        orderRepository.add(order)
    }

    suspend fun updateOrder(order: Order) {
        orderRepository.findById(customerId = order.customerId.value, orderDate = order.orderDate.toEpochMilli())
            ?: error("Order with customerId ${order.customerId} and orderDate '${order.orderDate}' already exists.")
        orderRepository.update(order)
    }

    suspend fun getOrder(customerId: CustomerId, orderDate: Instant): Order? {
        return orderRepository.findById(customerId = customerId.value, orderDate = orderDate.toEpochMilli())
    }

    suspend fun deleteOrder(customerId: CustomerId, orderDate: Instant) {
        orderRepository.deleteById(customerId = customerId.value, orderDate = orderDate.toEpochMilli())
    }

    suspend fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    suspend fun getAllOrdersOfProduct(productId: ProductId, from: Instant, to: Instant): List<Order> {
        require(from.isBefore(to)) { "from must be before to: $from" }
        return orderRepository.findAllByProductIdInRange(
            productId = productId.value,
            from =from,
            to = to
        )
    }

    suspend fun getAllOfPaymentType(paymentType: PaymentType, orderDate: Instant): List<Order> {
        return orderRepository.findAllByPaymentTypeUntil(
            paymentType = paymentType.name,
            until = orderDate,
        )
    }

}