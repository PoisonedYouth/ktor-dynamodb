package com.poisonedyouth.user

import dev.andrewohara.dynamokt.DataClassTableSchema
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.LocalDateTime
import java.time.ZoneOffset

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

    suspend fun findByJobStatus(jobStatus: JobStatus): List<User> {
        return buildList {
            table.scan(
                ScanEnhancedRequest.builder().filterExpression(
                    Expression.builder()
                        .expression("jobStatus = :jobStatus")
                        .putExpressionValue(":jobStatus", AttributeValue.builder().n(jobStatus.id.toString()).build())
                        .build()
                ).build()
            ).asFlow().collect { it.items().stream().forEach { item -> add(item.toUser()) } }
        }
    }

    suspend fun findByNameStartingWith(namePrefix: String): List<User> {
        return buildList {
            table.scan(
                ScanEnhancedRequest.builder().filterExpression(
                    Expression.builder()
                        .expression("begins_with(#nameAttr, :namePrefix)")
                        .putExpressionValue(":namePrefix", AttributeValue.builder().s(namePrefix).build())
                        .putExpressionName("#nameAttr", "name") // This is necessary because 'name' is a reserved word in DynamoDB
                        .build()
                ).build()
            ).asFlow().collect { it.items().stream().forEach { item -> add(item.toUser()) } }
        }
    }

    suspend fun findAllCreatedInRange(start: LocalDateTime, end: LocalDateTime): List<User> {
        return buildList {
            table.scan(
                ScanEnhancedRequest.builder().filterExpression(
                    Expression.builder()
                        .expression("createdAt BETWEEN :start AND :end")
                        .putExpressionValue(":start", AttributeValue.builder().s(start.toInstant(ZoneOffset.UTC).toString()).build())
                        .putExpressionValue(":end", AttributeValue.builder().s(end.toInstant(ZoneOffset.UTC).toString()).build())
                        .build()
                ).build()
            ).asFlow().collect { it.items().stream().forEach { item -> add(item.toUser()) } }
        }
    }

    suspend fun findAll(): List<User> {
        return buildList {
            table.scan().asFlow().collect { it.items().stream().forEach { item -> add(item.toUser()) } }
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
            dynamoDbEnhancedAsyncClient.batchGetItem(
                BatchGetItemEnhancedRequest.builder().readBatches(readBatch.build()).build()
            ).asFlow().collect { it.resultsForTable(table).stream().forEach { item -> add(item.toUser()) } }
        }

    }

    suspend fun batchWrite(userList: List<User>) {
        var writeBatch = WriteBatch.builder(
            UserEntity::class.java
        ).mappedTableResource(table)
        userList.forEach { user ->
            writeBatch.addPutItem(user.toUserEntity())
        }

        var unprocessedItems = dynamoDbEnhancedAsyncClient.batchWriteItem(
            BatchWriteItemEnhancedRequest.builder().writeBatches(writeBatch.build()).build()
        ).await().unprocessedPutItemsForTable(table)

        val maxRetryCount = 3
        var retry = 1
        while (unprocessedItems.isNotEmpty() && retry++ <= maxRetryCount) {
            writeBatch = WriteBatch.builder(
                UserEntity::class.java
            ).mappedTableResource(table)
            unprocessedItems.forEach { user ->
                writeBatch.addPutItem(user)
            }
            unprocessedItems = dynamoDbEnhancedAsyncClient.batchWriteItem(
                BatchWriteItemEnhancedRequest.builder().writeBatches(writeBatch.build()).build()
            ).await().unprocessedPutItemsForTable(table)
        }
        if (unprocessedItems.isNotEmpty()) {
            error("Not able to write items $unprocessedItems to database.")
        }

    }

}