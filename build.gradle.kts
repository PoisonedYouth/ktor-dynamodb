val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-1"
    kotlin("plugin.serialization") version "2.0.20"
}

group = "com.poisonedyouth"
version = "0.0.1"

application {
    mainClass.set("com.poisonedyouth.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io" )}
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.28.1")
    implementation("software.amazon.awssdk:dynamodb:2.28.1")
    implementation("com.github.oharaandrew314:dynamodb-kotlin-module:0.8.1")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
