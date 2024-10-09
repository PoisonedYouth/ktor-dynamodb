package com.poisonedyouth.user

import dev.andrewohara.dynamokt.DataClassTableSchema
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key

class UserRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) {
    private val tableName = UserEntity::class.simpleName
    private val tableSchema = DataClassTableSchema(UserEntity::class)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    suspend fun add(user: User): Unit = coroutineScope {
        table.putItem(user.toUserEntity()).await()
    }

    suspend fun findById(userId: Int): User? {
        return table.getItem(
            Key.builder().partitionValue(userId).build()
        ).await()?.toUser()
    }

    suspend fun findAll(): List<User> {
        return buildList {
            table.scan().subscribe { page ->
                page.items().stream().forEach { item -> add(item.toUser()) }
            }.await()
        }
    }
}