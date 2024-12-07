package org.brewcode.qa.spec.sample

import mu.KotlinLogging
import org.brewcode.qa.base.BaseSpec
import org.brewcode.qa.properties.BrewcodeProperties
import org.brewcode.qa.step.domain.auth.AuthStep
import org.brewcode.qa.step.domain.data.DataStep
import org.brewcode.qa.support.SpecTag.Scope
import org.brewcode.qa.support.SpecTag.Service
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired

private val log = KotlinLogging.logger {}

@Tags(Tag(Scope.E2E), Tag(Service.AUTH))
class SampleSpec : BaseSpec() {

    @Test
    fun scenarioWithSingleTestData(@Autowired properties: BrewcodeProperties, @Autowired authStep: AuthStep) {
        log.info { "Scenario: scenarioWithSingleTestData" }
        log.info { properties.name }
        authStep.login()
    }

    @Tag(Service.DATA)
    @ParameterizedTest
    @CsvSource(
        ignoreLeadingAndTrailingWhitespace = true,
        nullValues = ["NULL"],
        emptyValue = "EMPTY",
        value = [
            "1-1, 1-2",
            "NULL, 2-2",
            "3-1, EMPTY",
        ]
    )
    fun scenarioWithParameters(first: String?, second: String?, @Autowired properties: BrewcodeProperties, @Autowired dataStep: DataStep) {
        log.info { "Scenario: scenarioWithParameters: first=$first, second=$second" }
        log.info { properties.name }
        dataStep.shouldCreateItem()
    }
}