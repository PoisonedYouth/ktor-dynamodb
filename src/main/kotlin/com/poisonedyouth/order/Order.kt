@file:UseSerializers(InstantSerializer::class)

package com.poisonedyouth.order

import com.poisonedyouth.product.ProductId
import com.poisonedyouth.util.InstantSerializer
import dev.andrewohara.dynamokt.DynamoKtPartitionKey
import dev.andrewohara.dynamokt.DynamoKtSecondaryPartitionKey
import dev.andrewohara.dynamokt.DynamoKtSecondarySortKey
import dev.andrewohara.dynamokt.DynamoKtSortKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class OrderEntity(
    @DynamoKtPartitionKey
    val customerId: String,
    @DynamoKtSortKey
    @DynamoKtSecondarySortKey(indexNames = [PRODUCT_ID_ORDER_DATE_INDEX, PAYMENT_TYPE_ORDER_DATE_INDEX])
    val orderDate: Long,
    @DynamoKtSecondaryPartitionKey(indexNames = [PRODUCT_ID_ORDER_DATE_INDEX])
    val productId: String,
    val amount: Double,
    @DynamoKtSecondaryPartitionKey(indexNames = [PAYMENT_TYPE_ORDER_DATE_INDEX])
    val paymentType: String,
)

@Serializable
data class Order(
    val customerId: CustomerId,
    val orderDate: Instant,
    val productId: ProductId,
    val amount: Double,
    val paymentType: PaymentType,
)

enum class PaymentType {
    CREDIT_CARD,
    IN_ADVANCE,
    PAYPAL,
    BANK_TRANSFER
}

@JvmInline
@Serializable
value class CustomerId(val value: String) {
    init {
        require(value.isNotBlank()) { "CustomerId Id cannot be blank" }
        require(value.length == 16) { "CustomerId Id must be 16 characters" }
    }
}

fun OrderEntity.toOrder() = Order(
    customerId = CustomerId(customerId),
    productId = ProductId(productId),
    orderDate = Instant.ofEpochMilli(orderDate),
    amount = amount,
    paymentType = PaymentType.valueOf(paymentType)
)

fun Order.toOrderEntity() = OrderEntity(
    customerId = customerId.value,
    productId = productId.value,
    orderDate = orderDate.toEpochMilli(),
    amount = this.amount,
    paymentType = paymentType.name
)