package com.poisonedyouth.product

import dev.andrewohara.dynamokt.DynamoKtPartitionKey
import kotlinx.serialization.Serializable

data class ProductEntity(
    @DynamoKtPartitionKey
    val productId: String,
    val productName: String,
    val price: Double
)

@Serializable
data class Product(
    val productId: ProductId,
    val productName: ProductName,
    val price: Price
)

@JvmInline
@Serializable
value class ProductId(val value: String) {
    init {
        require(value.isNotBlank()) { "Product Id cannot be blank" }
        require(value.length == 16) { "Product Id must be 16 characters" }
    }
}

@JvmInline
@Serializable
value class ProductName(val value: String) {
    init {
        require(value.isNotBlank()) { "Product Name cannot be blank" }
        require(value.length <= 32) { "Product Name cannot be longer than 32 characters" }
    }
}

@JvmInline
@Serializable
value class Price(val value: Double) {
    init {
        require(value >= 0.0) { "Price must be positive." }
    }
}

fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        productId = productId.value,
        productName = productName.value,
        price = price.value
    )
}

fun ProductEntity.toProduct(): Product {
    return Product(
        productId = ProductId(productId),
        productName = ProductName(productName),
        price = Price(price)
    )
}