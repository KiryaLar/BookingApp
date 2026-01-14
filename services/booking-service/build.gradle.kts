plugins {
    // Convention plugins
    id("booking.spring-service")
}

description = "booking-service"

dependencies {
    // Base
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    // Lombok
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // Persistence
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)

    // Docs
    implementation(libs.springdoc.openapi.webmvc.ui)

    // Tests
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// OpenAPI codegen отключен: используем обычные контроллеры/DTO в src/main/java
