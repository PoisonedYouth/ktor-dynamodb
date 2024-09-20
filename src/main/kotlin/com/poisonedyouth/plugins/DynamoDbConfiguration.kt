package com.poisonedyouth.plugins

import com.poisonedyouth.product.ProductEntity
import dev.andrewohara.dynamokt.DataClassTableSchema
import io.ktor.server.application.Application
import kotlinx.coroutines.future.await
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

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
    val logger = LoggerFactory.getLogger(Application::class.java)

    val existingTables = dynamoDbClient.listTables().await().tableNames()

    val productEntity = ProductEntity::class
    val tableSchema = DataClassTableSchema(productEntity)
    if (existingTables.contains(productEntity.simpleName)) {
        logger.info("Table '${productEntity.simpleName}' already exists.")
    } else {
        dynamoDbEnhancedClient.table(productEntity.simpleName, tableSchema).createTable().await()
        logger.info("Table '${productEntity.simpleName}' created successfully.")
    }
}