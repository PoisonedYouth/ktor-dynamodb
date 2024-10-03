package com.poisonedyouth.order

import com.poisonedyouth.product.toProduct
import dev.andrewohara.dynamokt.DataClassTableSchema
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

const val PRODUCT_ID_ORDER_DATE_INDEX = "ProductId-OrderDate-index"
const val PAYMENT_TYPE_ORDER_DATE_INDEX = "PaymentType-OrderDate-index"

class OrderRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) {
    private val tableName = OrderEntity::class.simpleName
    private val tableSchema = DataClassTableSchema(OrderEntity::class)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    suspend fun add(order: Order): Unit = coroutineScope {
        table.putItem(order.toOrderEntity()).await()
    }

    suspend fun findAllByProductIdInRange(productId: String, from: Instant, to: Instant): List<Order>{
        return buildList {
            table.index(PRODUCT_ID_ORDER_DATE_INDEX).query(
                QueryConditional.sortBetween(
                    Key.builder().partitionValue(productId).sortValue(from.toEpochMilli()).build(),
                    Key.builder().partitionValue(productId).sortValue(to.toEpochMilli()).build()
                )
            ).asFlow().collect { it.items().stream().forEach { item -> add(item.toOrder()) } }
        }
    }

    suspend fun findAllByPaymentTypeUntil(paymentType: String, until: Instant): List<Order>{
        return buildList {
            table.index(PAYMENT_TYPE_ORDER_DATE_INDEX).query(
                QueryConditional.sortLessThanOrEqualTo(
                    Key.builder().partitionValue(paymentType).sortValue(until.toEpochMilli()).build(),
                )
            ).subscribe { page ->
                page.items().stream().forEach { item -> add(item.toOrder()) }
            }.await()
        }
    }

    suspend fun findById(customerId: String, orderDate: Long): Order? {
        return table.getItem(
            Key.builder().partitionValue(customerId).sortValue(orderDate).build()
        ).await()?.toOrder()
    }

    suspend fun findStartingWith(customerId: String, localDate: LocalDate): List<Order> {
        return buildList {
            table.query(
                QueryConditional.sortBeginsWith(
                    Key.builder().partitionValue(customerId).sortValue(localDate.toEpochSecond(
                        LocalTime.MIN, ZoneOffset.UTC
                    )).build(),
                )
            ).subscribe { page ->
                page.items().stream().forEach { item -> add(item.toOrder()) }
            }.await()
        }
    }

    suspend fun findInRange(customerId: String, from: Long, to: Long): List<Order> {
        return buildList {
            table.query(
                QueryConditional.sortBetween(
                    Key.builder().partitionValue(customerId).sortValue(from).build(),
                    Key.builder().partitionValue(customerId).sortValue(to).build()
                )
            ).subscribe { page ->
                page.items().stream().forEach { item -> add(item.toOrder()) }
            }.await()
        }

    }

    suspend fun findBefore(customerId: String, orderDate: Long): List<Order> {
        return buildList {
            table.query(
                QueryConditional.sortLessThan(
                    Key.builder().partitionValue(customerId).sortValue(orderDate).build(),
                )
            ).subscribe() { page ->
                page.items().stream().forEach { item -> add(item.toOrder()) }
            }.await()
        }

    }

    suspend fun findAll(): List<Order> {
        return buildList {
            table.scan().subscribe { page ->
                page.items().stream().forEach { item -> add(item.toOrder()) }
            }.await()
        }
    }

    suspend fun deleteById(customerId: String, orderDate: Long) {
        table.deleteItem(Key.builder().partitionValue(customerId).sortValue(orderDate).build()).await()
    }

    suspend fun update(order: Order) {
        table.updateItem(order.toOrderEntity()).await()
    }
}