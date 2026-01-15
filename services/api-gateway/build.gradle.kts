plugins {
//    Convention plugins from build-logic
    id("booking.spring-service")
}

description = "api-gateway"

dependencies {
//    Base
    implementation(libs.spring.boot.starter.webflux)

//    Gateway
    implementation(libs.spring.cloud.starter.gateway)

//    Eureka Client
    implementation(platform(libs.spring.cloud.bom))
    implementation(libs.spring.cloud.starter.netflix.eureka.client)

//    Observability
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.otlp)

//    Security
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jjwt.api)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)
    implementation(libs.spring.security.oauth2.jose)

//    OpenApi
    implementation(libs.springdoc.openapi.webflux.ui)

//    Tests
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.wiremock)
}
