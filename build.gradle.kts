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

tasks.test {
    useJUnitPlatform()
    include("**/*Test.class")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.4") // Request DTO 유효성 검증
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.4.4") // 구글 로그인 연동

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // JWT 라이브러리 추가
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("org.postgresql:postgresql:42.7.5")


    testImplementation("org.springframework.security:spring-security-test:6.4.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.4") {
        exclude(group = "org.mockito")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine") // vintage 엔진 제거
    }

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.1.20")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2") // JUnit5
    testImplementation("org.testcontainers:postgresql")

    testImplementation("net.java.dev.jna:jna:5.17.0")
    testImplementation("net.java.dev.jna:jna-platform:5.17.0")

    testImplementation("io.mockk:mockk:1.14.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


sourceSets {
    test {
        java {
            setSrcDirs(listOf("src/test/intg", "src/test//unit"))
        }
    }
}