plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation") // ✨ Request DTO 유효성 검증
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client") // ✨ 구글 로그인 연동
    implementation("com.auth0:java-jwt:4.4.0") // JWT 발급용

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // 🔐 JWT 라이브러리 추가
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // JSON 처리용 Jackson 바인딩
    runtimeOnly("org.postgresql:postgresql")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:postgresql")

    testImplementation("io.mockk:mockk:1.13.10") // 최신 버전 기준 (2025년 4월 기준 안정 버전)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1") // JUnit5
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito") // Mockito 제외 가능
    }
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
            java.setSrcDirs(listOf("src/test/kotlin"))
        }
    }
}