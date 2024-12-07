package org.brewcode.qa.step.domain.auth

import org.brewcode.qa.step.base.TestStep
import org.springframework.stereotype.Component

@Component
class AuthStep : TestStep {

    fun login() {
        println("login")
    }
}