package com.poisonedyouth.user

import dev.andrewohara.dynamokt.DynamoKtConverted
import dev.andrewohara.dynamokt.DynamoKtPartitionKey
import kotlinx.serialization.Serializable

data class UserEntity(
    @DynamoKtPartitionKey
    val userId: Int,
    val name: String,
    val email: String,
    @DynamoKtConverted(AddressConverter::class)
    val address: AddressEntity
)

@Serializable
data class User(
    val userId: UserId,
    val name: Name,
    val email: Email,
    val address: Address,
)

@JvmInline
@Serializable
value class Email(val value: String) {
    init {
        require(EMAIL_REGEX.matches(value)) { "Email must be valid." }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[^@]+@[^@]+\\.[^@]+\$")
    }
}

@JvmInline
@Serializable
value class Name(val value: String) {
    init {
        require(value.isNotEmpty()) { "Name must not be empty" }
        require(value.length <= MAX_NAME_LENGTH) { "Name must have at maximum $MAX_NAME_LENGTH characters" }
    }

    companion object {
        private const val MAX_NAME_LENGTH = 16
    }
}

@JvmInline
@Serializable
value class UserId(val value: Int) {
    init {
        require(value in MIN_USER_ID..MAX_USER_ID) { "UserId must be between $MIN_USER_ID and $MAX_USER_ID." }
    }

    companion object {
        private const val MIN_USER_ID = 10000
        private const val MAX_USER_ID = 99999
    }
}

fun UserEntity.toUser() = User(
    userId = UserId(userId),
    name = Name(name),
    email = Email(email),
    address = address.toAddress()
)

fun User.toUserEntity() = UserEntity(
    userId = userId.value,
    name = name.value,
    email = email.value,
    address = address.toAddressEntity()
)