import de.fayard.refreshVersions.RefreshVersionsMigrateTask
import de.fayard.refreshVersions.core.versionFor
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2

//// PLUGINS ////
plugins {
    kotlin("jvm")
    id("idea")
    id("org.springframework.boot") apply false
    id("org.jetbrains.kotlin.plugin.spring") apply false
    id("io.qameta.allure") apply true // https://github.com/allure-framework/allure-gradle
}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management") // https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html
apply(plugin = "org.jetbrains.kotlin.plugin.spring")

//// GRADLE.PROPERTIES ////
val javaVersion: String by project
val wrapperVersion: String by project

//// WRAPPER ////
tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = wrapperVersion
}

//// OTHER ////
tasks.withType<RefreshVersionsMigrateTask> {
    mode = RefreshVersionsMigrateTask.Mode.VersionsPropertiesOnly
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

allure {
    adapter {
        autoconfigure = false
        autoconfigureListeners = false
        aspectjWeaver = true
    }
    commandline {
        version = versionFor("version.io.qameta.allure..allure-commandline")
    }
}

tasks.named("allureReport") {
    doFirst { delete(allure.report.reportDir.get()) }
    notCompatibleWithConfigurationCache("Allure Report")
}

tasks.named("downloadAllure") {
    notCompatibleWithConfigurationCache("Allure Report")
}

tasks.withType<Test>().configureEach {
    notCompatibleWithConfigurationCache("Allure Report")
}

//// KOTLIN ////
kotlin {
    compilerOptions {
        suppressWarnings = true
        jvmTarget.set(JVM_21)
        languageVersion.set(KOTLIN_2_2)
        apiVersion.set(KOTLIN_2_2)
    }
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

//// TEST ////
tasks.withType<Test>().configureEach {
    doFirst {
        delete("build/allure-results")
    }
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
    outputs.upToDateWhen { false }
}

//// DEPENDENCIES ////
repositories {
    mavenCentral()
}

// Disable transitive dependencies
val useTransitiveDependenciesForThisLibs = arrayOf("org.jetbrains.kotlin")
configurations.all {
    withDependencies {
        forEach { dependency ->
            if (dependency is ModuleDependency && dependency.group !in useTransitiveDependenciesForThisLibs)
                dependency.isTransitive = false
        }
    }
}

dependencies {
    /* Kotlin */
    implementation("org.jetbrains.kotlin:kotlin-reflect:_")

    /* Spring Boot Bom */
    testImplementation("org.springframework.boot:spring-boot-test:_")
    implementation("org.springframework.boot:spring-boot:_")
    // Spring Modules //
    testImplementation("org.springframework:spring-test:_")
    implementation("org.springframework:spring-context:_")
    implementation("org.springframework:spring-core:_")
    implementation("org.springframework:spring-beans:_")
    implementation("org.springframework:spring-aop:_")
    implementation("org.springframework:spring-expression:_")
    // Junit //
    testImplementation(Testing.junit.jupiter)
    testImplementation(Testing.junit.jupiter.api)
    testImplementation(Testing.junit.jupiter.params)
    testRuntimeOnly(Testing.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-engine:_")
    testRuntimeOnly("org.junit.platform:junit-platform-commons:_")
    // Other //
    implementation("org.slf4j:slf4j-api:_")
    implementation("org.yaml:snakeyaml:_")

    /* Other Libs Out of Spring Dependencies Management */
    implementation("io.github.microutils:kotlin-logging-jvm:_") // https://github.com/oshai/kotlin-logging
    implementation("commons-logging:commons-logging:_")
    implementation("org.opentest4j:opentest4j:_")
    // Allure for Junit5 //
    implementation("org.aspectj:aspectjweaver:_")
    implementation("io.qameta.allure:allure-java-commons:_")
    implementation("io.qameta.allure:allure-model:_")
    testImplementation("io.qameta.allure:allure-junit5:_")
    testImplementation("io.qameta.allure:allure-junit-platform:_")
    testImplementation("io.qameta.allure:allure-test-filter:_")
    // Assertions - kotest //
    testImplementation(Testing.kotest.assertions.core)
}
