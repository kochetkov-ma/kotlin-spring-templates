import de.fayard.refreshVersions.RefreshVersionsMigrateTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2

//// PLUGINS ////
plugins {
    id("idea")
    kotlin("jvm")
    id("org.springframework.boot") apply false // https://docs.spring.io/spring-boot/gradle-plugin/getting-started.html
    id("org.jetbrains.kotlin.plugin.spring") apply false // https://kotlinlang.org/docs/all-open-plugin.html
}

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

//// ROOT GRADLE.PROPERTIES ////
val javaVersion: String by project
val wrapperVersion: String by project

//// ROOT REPOSITORY ////
repositories { mavenCentral() }

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management") // https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    //// PROJECT GRADLE.PROPERTIES ////
    val useSpring: Boolean = project.properties.getOrDefault("useSpring", "true").toString().toBoolean()
    val useJunit5: Boolean = project.properties.getOrDefault("useJunit5", "true").toString().toBoolean()

    //// KOTLIN ////
    kotlin {
        compilerOptions {
            suppressWarnings = true
            jvmTarget.set(JVM_21)
            languageVersion.set(KOTLIN_2_2)
            apiVersion.set(KOTLIN_2_2)
        }
        jvmToolchain { languageVersion = JavaLanguageVersion.of(javaVersion) }
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    //// TEST ////
    if (useJunit5)
        tasks.withType<Test>().configureEach {
            useJUnitPlatform { includeEngines("junit-jupiter") }
            outputs.upToDateWhen { false }
        }

    //// DEPENDENCIES ////
    repositories { mavenCentral() }

    // Disable transitive dependencies //
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
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        if (useSpring) {
            testImplementation("org.springframework.boot:spring-boot-test")
            implementation("org.springframework.boot:spring-boot")
            // Spring Modules //
            testImplementation("org.springframework:spring-test")
            implementation("org.springframework:spring-context")
            implementation("org.springframework:spring-core")
            implementation("org.springframework:spring-beans")
            implementation("org.springframework:spring-aop")
            implementation("org.springframework:spring-expression")
            // Other //
            implementation("org.yaml:snakeyaml")
        }

        if (useJunit5) {
            testImplementation("org.junit.jupiter:junit-jupiter")
            testImplementation("org.junit.jupiter:junit-jupiter-api")
            testImplementation("org.junit.jupiter:junit-jupiter-params")
            testImplementation("org.junit.platform:junit-platform-commons")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
            testRuntimeOnly("org.junit.platform:junit-platform-engine")
            testRuntimeOnly("org.junit.platform:junit-platform-commons")
            testRuntimeOnly("commons-logging:commons-logging:_")
            testImplementation("org.opentest4j:opentest4j:_")
        }

        /* Common Libs from Spring Dependencies Management */
        implementation("org.slf4j:slf4j-api")
        implementation("ch.qos.logback:logback-classic")
        implementation("ch.qos.logback:logback-core")

        /* Other Libs Out of Spring Dependencies Management */
        implementation("io.github.microutils:kotlin-logging-jvm:_") // https://github.com/oshai/kotlin-logging
    }
}