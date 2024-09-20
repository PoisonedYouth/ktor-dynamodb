package com.poisonedyouth.product

import dev.andrewohara.dynamokt.DataClassTableSchema
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key

class ProductRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) {
    private val tableName = ProductEntity::class.simpleName
    private val tableSchema = DataClassTableSchema(ProductEntity::class)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    suspend fun add(product: Product): Unit = coroutineScope {
        table.putItem(product.toProductEntity()).await()
    }

    suspend fun findById(productId: String): Product? {
        return table.getItem(
            Key.builder().partitionValue(productId).build()
        ).await()?.toProduct()
    }

    suspend fun findAll(): List<Product> {
        return buildList {
            table.scan().subscribe { page ->
                page.items().stream().forEach { item -> add(item.toProduct()) }
            }.await()
        }
    }

    suspend fun deleteById(productId: String) {
        table.deleteItem(Key.builder().partitionValue(productId).build()).await()
    }

    suspend fun updateProduct(product: Product) {
        table.updateItem(product.toProductEntity()).await()
    }
}