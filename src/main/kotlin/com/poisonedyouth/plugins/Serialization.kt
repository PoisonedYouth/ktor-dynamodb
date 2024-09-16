package com.poisonedyouth.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                // Enable certain features
                encodeDefaults = true
                ignoreUnknownKeys = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true

                // Set naming strategy
                useArrayPolymorphism = false
                classDiscriminator = "class"
            },
        )
    }
}