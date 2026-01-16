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

    // Security
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)

    // Persistence
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)

    //    Observability
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.otlp)

    // Docs
    implementation(libs.springdoc.openapi.webmvc.ui)

    // Tests
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly(libs.junit.platform.launcher)
}
