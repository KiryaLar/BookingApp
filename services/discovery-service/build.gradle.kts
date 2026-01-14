plugins {
    // Convention plugins
    id("booking.spring-service")
}

description = "discovery-service"

dependencies {
//    Base
    implementation(libs.spring.boot.starter.web)

//    Eureka Server
    implementation(platform(libs.spring.cloud.bom))
    implementation(libs.spring.cloud.starter.netflix.eureka.server)

//    Actuator
    implementation(libs.spring.boot.starter.actuator)
}
