package org.brewcode.qa.base

import io.qameta.allure.junit5.AllureJunit5
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [BaseSpecConfiguration::class])
@ExtendWith(AllureJunit5::class)
class BaseSpec