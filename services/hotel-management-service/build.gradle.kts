plugins {
    // Convention plugins
    id("booking.spring-service")
}

description = "hotel-management-service"

dependencies {
//    Base
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    // Lombok
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.lombok)

//    Persistence
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)

    testRuntimeOnly(libs.junit.platform.launcher)

    // Docs
    implementation(libs.springdoc.openapi.webmvc.ui)

    // Tests
    testRuntimeOnly(libs.junit.platform.launcher)
}