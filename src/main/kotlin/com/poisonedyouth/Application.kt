package com.poisonedyouth

import com.poisonedyouth.order.OrderRepository
import com.poisonedyouth.order.OrderService
import com.poisonedyouth.order.configureRouting
import com.poisonedyouth.plugins.*
import com.poisonedyouth.product.ProductRepository
import com.poisonedyouth.product.ProductService
import com.poisonedyouth.product.configureRouting
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() = runBlocking {
    val dynamoDbClient = createDynamoDbClient()
    val dynamoDbEnhancedClient = createEnhancedDynamoDbClient(dynamoDbClient)
    createNecessaryTables(dynamoDbClient, dynamoDbEnhancedClient)

    configureSerialization()

    val productRepository = ProductRepository(dynamoDbEnhancedClient)
    val productService = ProductService(productRepository)
    configureRouting(productService)
    val orderRepository = OrderRepository(dynamoDbEnhancedClient)
    val orderService = OrderService(orderRepository)
    configureRouting(orderService)
}
