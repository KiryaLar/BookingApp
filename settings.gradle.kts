rootProject.name = "BookingApp"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(
    "services:api-gateway",
    "services:hotel-management-service",
    "services:booking-service",
    "services:discovery-service",
//    "libs:common"
//    , "config-service"
)