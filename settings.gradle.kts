rootProject.name = "BookingApp"

pluginManagement {
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

includeBuild("build-logic")

include(
    "services:api-gateway",
    "services:hotel-management-service",
    "services:booking-service",
    "services:discovery-service",
//    "libs:common"
//    , "config-service"
)