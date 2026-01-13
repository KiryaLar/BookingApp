plugins {
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.openapi.generator)
    java
}

description = "booking-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val specFile = file("$projectDir/src/main/resources/openapi/openapi.yaml").absolutePath
val genDir = layout.buildDirectory.dir("generated/openapi")

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set(specFile)
    outputDir.set(genDir.get().asFile.absolutePath)

    apiPackage.set("ru.larkin.bookingservice.api")
    modelPackage.set("ru.larkin.bookingservice.model")
    invokerPackage.set("ru.larkin.bookingservice.invoker")

    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "dateLibrary" to "java8",
            "useBeanValidation" to "true",
            "useSpringBoot3" to "true",
            "useTags" to "true",
            "useJakartaEe" to "true",
            "openApiNullable" to "false"
        )
    )

    globalProperties.set(
        mapOf(
            "models" to "",
            "apis" to "",
            "supportingFiles" to "false",
            "apiDocs" to "false",
            "modelDocs" to "false",
            "apiTests" to "false",
            "modelTests" to "false"
        )
    )
}

sourceSets {
    main {
        java {
            srcDir(genDir.map { it.dir("src/main/java") } )
        }
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}