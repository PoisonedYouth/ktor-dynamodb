package com.poisonedyouth.user

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class JobStatusConverter : AttributeConverter<JobStatus> {

    override fun transformFrom(input: JobStatus): AttributeValue {
        return AttributeValue.builder().n(input.id.toString()).build()
    }

    override fun transformTo(input: AttributeValue): JobStatus {
        return JobStatus.fromId(input.n().toInt())
    }

    override fun type(): EnhancedType<JobStatus> = EnhancedType.of(JobStatus::class.java)

    override fun attributeValueType(): AttributeValueType = AttributeValueType.N
}