package com.poisonedyouth.user

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class LocalDateTimeConverter : AttributeConverter<LocalDateTime> {

    override fun transformFrom(input: LocalDateTime): AttributeValue {
        return AttributeValue.builder().s(input.toInstant(ZoneOffset.UTC).toString()).build()
    }

    override fun transformTo(input: AttributeValue): LocalDateTime {
        return Instant.parse(input.s()).atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime()
    }

    override fun type(): EnhancedType<LocalDateTime> = EnhancedType.of(LocalDateTime::class.java)

    override fun attributeValueType(): AttributeValueType = AttributeValueType.S
}