plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

task.withType<Test>.configureEach {
    useJUnitPlatform()
}