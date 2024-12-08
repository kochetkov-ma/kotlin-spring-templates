package org.brewcode.qa.step.domain.data

import io.qameta.allure.Step
import org.brewcode.qa.step.base.TestCheckStep
import org.springframework.stereotype.Component

@Component
class DataStep : TestCheckStep {

    @Step("Item should be created")
    fun shouldCreateItem() {
        println("dataStep")
    }
}