package org.brewcode.qa.step.domain.auth

import io.qameta.allure.Step
import org.brewcode.qa.step.base.TestStep
import org.springframework.stereotype.Component

@Component
class AuthStep : TestStep {

    @Step("Login")
    fun login() {
        println("login")
    }
}