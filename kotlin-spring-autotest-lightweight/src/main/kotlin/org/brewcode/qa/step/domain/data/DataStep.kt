package org.brewcode.qa.step.domain.data

import org.brewcode.qa.step.base.TestCheckStep
import org.springframework.stereotype.Component

@Component
class DataStep : TestCheckStep {

    fun shouldCreateItem() {
        println("dataStep")
    }
}