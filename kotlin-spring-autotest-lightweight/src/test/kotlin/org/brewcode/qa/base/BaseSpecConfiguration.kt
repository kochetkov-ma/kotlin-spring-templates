package org.brewcode.qa.base

import org.brewcode.qa.properties.BrewcodeProperties
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(BrewcodeProperties::class)
@ComponentScan("org.brewcode.qa.step")
@SpringBootConfiguration
class BaseSpecConfiguration