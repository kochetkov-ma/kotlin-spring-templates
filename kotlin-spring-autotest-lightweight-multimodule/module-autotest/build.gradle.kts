import de.fayard.refreshVersions.core.versionFor

plugins {
    id("io.qameta.allure") apply true // https://github.com/allure-framework/allure-gradle
}

allure {
    adapter {
        autoconfigure = false
        autoconfigureListeners = false
        aspectjWeaver = true
    }
    commandline {
        version = versionFor("version.allure-commandline")
    }
}

tasks.named("allureReport") {
    doFirst { delete(allure.report.reportDir.get()) }
}

tasks.withType<Test>().configureEach {
    doFirst { delete("$projectDir/build/allure-results") }
}

dependencies {

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