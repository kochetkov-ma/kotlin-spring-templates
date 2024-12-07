package org.brewcode.qa.spec.auth

import mu.KotlinLogging
import org.brewcode.qa.base.BaseSpec
import org.brewcode.qa.properties.BrewcodeProperties
import org.brewcode.qa.step.domain.auth.AuthStep
import org.brewcode.qa.support.SpecSupport
import org.brewcode.qa.support.SpecTag.Scope
import org.brewcode.qa.support.SpecTag.Service
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

private val log = KotlinLogging.logger {}

@Tags(Tag(Scope.API), Tag(Service.AUTH))
class AuthApiSpec : BaseSpec() {

    @Test
    fun scenarioAuth(@Autowired properties: BrewcodeProperties, @Autowired authStep: AuthStep) {
        log.info { "Scenario: scenarioAuth" }
        log.info { properties.name }

        SpecSupport.supportStaticFunction()
        authStep.login()
    }
}