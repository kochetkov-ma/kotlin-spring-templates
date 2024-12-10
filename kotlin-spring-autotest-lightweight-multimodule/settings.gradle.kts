rootProject.name = "kotlin-spring-autotest-lightweight-multimodule"

plugins {
    id("de.fayard.refreshVersions") version "0.60.5" // https://splitties.github.io/refreshVersions/
}

include("module-autotest")
include("module-code")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")