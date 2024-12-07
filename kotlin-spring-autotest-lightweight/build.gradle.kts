import de.fayard.refreshVersions.RefreshVersionsMigrateTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2

//// PLUGINS ////
plugins {
    kotlin("jvm")
    id("idea")
    id("org.springframework.boot") apply false
    id("org.jetbrains.kotlin.plugin.spring") apply false
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
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
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
            if (dependency is ModuleDependency && dependency.group !in useTransitiveDependenciesForThisLibs) {
                dependency.isTransitive = false
            }
        }
    }
}

dependencies {
    /* Kotlin */
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    /* Spring Boot Bom */
    testImplementation("org.springframework.boot:spring-boot-test")
    implementation("org.springframework.boot:spring-boot")
    // Spring Modules //
    testImplementation("org.springframework:spring-test")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-beans")
    implementation("org.springframework:spring-aop")
    implementation("org.springframework:spring-expression")
    // Junit //
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-commons")
    // Other //
    implementation("org.slf4j:slf4j-api")
    implementation("org.yaml:snakeyaml")

    /* Other Libs Out of Spring Dependencies Management */
    implementation("io.github.microutils:kotlin-logging-jvm:_") // https://github.com/oshai/kotlin-logging
    implementation("commons-logging:commons-logging:_")
    implementation("org.opentest4j:opentest4j:_")
}
