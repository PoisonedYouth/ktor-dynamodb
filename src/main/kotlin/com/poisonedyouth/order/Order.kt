package com.poisonedyouth.order

import com.poisonedyouth.product.ProductId
import dev.andrewohara.dynamokt.DynamoKtPartitionKey
import dev.andrewohara.dynamokt.DynamoKtSortKey
import kotlinx.serialization.Serializable

data class OrderEntity(
    @DynamoKtPartitionKey
    val orderId: String,
    @DynamoKtSortKey
    val productId: String,
    val amount: Double,
)

@Serializable
data class Order(
    val orderId: OrderId,
    val productId: ProductId,
    val amount: Double,
)

@JvmInline
@Serializable
value class OrderId(val value: String) {
    init {
        require(value.isNotBlank()) { "Order Id cannot be blank" }
        require(value.length == 16) { "Order Id must be 16 characters" }
    }
}

fun OrderEntity.toOrder() = Order(
    orderId = OrderId(this.orderId),
    productId = ProductId(this.productId),
    amount = this.amount
)

fun Order.toOrderEntity() = OrderEntity(
    orderId = this.orderId.value,
    productId = this.productId.value,
    amount = this.amount
)