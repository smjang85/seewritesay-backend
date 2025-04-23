import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.internal.types.error.ErrorModuleDescriptor.platform

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.lena"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.43.0@jar")
    implementation("com.google.firebase:firebase-admin:9.4.3")

    implementation("com.nimbusds:nimbus-jose-jwt:10.2") // 최신 버전 확인해도 됨
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")


    // JWT
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("org.postgresql:postgresql:42.7.5")

    // ✅ JUnit BOM으로 버전 통일
    testImplementation(platform("org.junit:junit-bom:5.12.2"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("net.java.dev.jna:jna:5.17.0")
    testImplementation("net.java.dev.jna:jna-platform:5.17.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/resources/firebase-service-account.json") {
        into("/")
    }
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
    test {
        java {
            setSrcDirs(listOf("src/test/intg", "src/test/unit"))
        }
    }
}
