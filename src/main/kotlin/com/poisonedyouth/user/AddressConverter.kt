package com.poisonedyouth.user

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class AddressConverter : AttributeConverter<AddressEntity> {

    override fun transformFrom(input: AddressEntity): AttributeValue {
        val json = Json.encodeToString(input)
        return AttributeValue.builder().s(json).build()
    }

    override fun transformTo(input: AttributeValue): AddressEntity {
        return Json.decodeFromString(input.s())
    }

    override fun type(): EnhancedType<AddressEntity> = EnhancedType.of(AddressEntity::class.java)

    override fun attributeValueType(): AttributeValueType = AttributeValueType.S
}