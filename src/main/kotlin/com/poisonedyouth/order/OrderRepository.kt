package com.poisonedyouth.order

import dev.andrewohara.dynamokt.DataClassTableSchema
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key

class OrderRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) {
    private val tableName = OrderEntity::class.simpleName
    private val tableSchema = DataClassTableSchema(OrderEntity::class)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    suspend fun add(order: Order): Unit = coroutineScope {
        table.putItem(order.toOrderEntity()).await()
    }

    suspend fun findById(orderId: String, productId: String): Order? {
        return table.getItem(
            Key.builder().partitionValue(orderId).sortValue(productId).build()
        ).await()?.toOrder()
    }

    suspend fun findAll(): List<Order> {
        return buildList {
            table.scan().subscribe { page ->
                page.items().stream().forEach { item -> add(item.toOrder()) }
            }.await()
        }
    }

    suspend fun deleteById(orderId: String, productId: String) {
        table.deleteItem(Key.builder().partitionValue(orderId).sortValue(productId).build()).await()
    }

    suspend fun update(order: Order) {
        table.updateItem(order.toOrderEntity()).await()
    }
}