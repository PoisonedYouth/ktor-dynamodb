package com.poisonedyouth.user

import kotlinx.serialization.Serializable

@Serializable
data class AddressEntity(
    val street: String,
    val city: String,
    val state: String,
    val zip: Int
)

@Serializable
data class Address(
    val street: Street,
    val city: City,
    val state: State,
    val zip: Zip
)

@JvmInline
@Serializable
value class Zip(val value: Int) {
    init {
        require(value in MIN_ZIP_CODE..MAX_ZIP_CODE) { "Zip must be between $MIN_ZIP_CODE and $MAX_ZIP_CODE." }
    }

    companion object {
        const val MIN_ZIP_CODE = 10000
        const val MAX_ZIP_CODE = 99999
    }
}

@JvmInline
@Serializable
value class State(val value: String) {
    init {
        require(value.isNotBlank()) { "State must not be empty." }
    }
}

@JvmInline
@Serializable
value class Street(val value: String) {
    init {
        require(value.isNotBlank()) { "Street must not be empty." }
    }
}

@JvmInline
@Serializable
value class City(val value: String) {
    init {
        require(value.isNotBlank()) { "City must not be empty." }
    }
}

fun AddressEntity.toAddress() = Address(
    street = Street(street),
    city = City(city),
    state = State(state),
    zip = Zip(zip)
)

fun Address.toAddressEntity() = AddressEntity(
    street = street.value,
    city = city.value,
    state = state.value,
    zip = zip.value
)