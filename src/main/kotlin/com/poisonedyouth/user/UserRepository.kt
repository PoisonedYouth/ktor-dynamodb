package com.poisonedyouth.user

import dev.andrewohara.dynamokt.DataClassTableSchema
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch

class UserRepository(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) {
    private val tableName = UserEntity::class.simpleName
    private val tableSchema = DataClassTableSchema(UserEntity::class)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    suspend fun add(user: User): Unit = coroutineScope {
        table.putItem(user.toUserEntity()).await()
    }

    suspend fun findById(userId: UserId): User? {
        return table.getItem(
            Key.builder().partitionValue(userId.value).build()
        ).await()?.toUser()
    }

    suspend fun findAll(): List<User> {
        return buildList {
            table.scan().asFlow().collect{ it.items().stream().forEach { item -> add(item.toUser()) } }
        }
    }

    suspend fun batchGet(userIds: List<UserId>): List<User> {
        val readBatch = ReadBatch.builder(
            UserEntity::class.java
        ).mappedTableResource(table)
        userIds.forEach { userId ->
            readBatch.addGetItem(Key.builder().partitionValue(userId.value).build())
        }
        return buildList {
            dynamoDbEnhancedAsyncClient.batchGetItem {
                BatchGetItemEnhancedRequest.builder().readBatches(readBatch.build()).build()
            }.asFlow().collect{ it.resultsForTable(table).stream().forEach { item -> add(item.toUser()) } }
        }

    }
}