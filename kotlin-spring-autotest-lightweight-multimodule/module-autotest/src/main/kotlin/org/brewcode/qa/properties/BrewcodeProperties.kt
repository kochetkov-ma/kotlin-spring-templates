package org.brewcode.qa.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("brewcode")
data class BrewcodeProperties(
    val name: String
)
