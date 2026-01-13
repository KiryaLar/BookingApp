plugins {
    id("java-library")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt())) }
}

tasks.test { useJUnitPlatform() }

dependencies {
    // сюда — общие зависимости DTO/утилит по необходимости
    // пример: api(libs.some) или implementation(libs.other)
}