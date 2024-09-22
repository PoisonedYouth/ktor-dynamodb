package com.poisonedyouth.plugins

import com.poisonedyouth.order.OrderEntity
import com.poisonedyouth.product.ProductEntity
import dev.andrewohara.dynamokt.DataClassTableSchema
import io.ktor.server.application.Application
import kotlinx.coroutines.future.await
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI
import kotlin.reflect.KClass

fun Application.createDynamoDbClient(): DynamoDbAsyncClient {
    val url = environment.config.property("ktor.database.dynamodbUrl").getString()

    return DynamoDbAsyncClient.builder()
        .endpointOverride(URI(url)) // Local DynamoDB
        .build()
}

fun createEnhancedDynamoDbClient(dynamoDbClient: DynamoDbAsyncClient): DynamoDbEnhancedAsyncClient {
    // Create an enhanced client using the low-level client
    return DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dynamoDbClient)
        .build()
}

suspend fun createNecessaryTables(dynamoDbClient: DynamoDbAsyncClient, dynamoDbEnhancedClient: DynamoDbEnhancedAsyncClient) {
    val existingTables = dynamoDbClient.listTables().await().tableNames()

    listOf(ProductEntity::class, OrderEntity::class).forEach {
        createTableIfNotExists(existingTables, it, dynamoDbEnhancedClient)
    }
}

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

private suspend fun <T : Any> createTableIfNotExists(existingTables: List<String>, item: KClass<T>, dynamoDbEnhancedClient: DynamoDbEnhancedAsyncClient) {
    val tableSchema = DataClassTableSchema(item)
    if (existingTables.contains(item.simpleName)) {
        logger.info("Table '${item.simpleName}' already exists.")
    } else {
        dynamoDbEnhancedClient.table(item.simpleName, tableSchema).createTable().await()
        logger.info("Table '${item.simpleName}' created successfully.")
    }
}
