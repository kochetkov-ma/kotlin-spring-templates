package org.brewcode.qa.support

import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.model.Parameter
import io.qameta.allure.model.Status
import io.qameta.allure.model.StepResult
import io.qameta.allure.util.ResultsUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import java.util.*

private typealias DescriptionProvider = () -> String

@Aspect
class KotestAssertionsAspects {

    private val activeStepUuid = ThreadLocal<String?>()

    private companion object {
        private val DISABLED: Boolean = (System.getProperty("assert.aspects.disabled") ?: System.getenv("ASSERT_ASPECTS_DISABLED")) != null
        private val allure: AllureLifecycle = Allure.getLifecycle()
    }

    @Pointcut("call(public * io.kotest.assertions.ErrorCollector.pushClue(..))")
    fun pushCluePointcut() {
    }

    @Pointcut("call(public * io.kotest.assertions.ErrorCollector.popClue(..))")
    fun popCluePointcut() {
    }

    @Pointcut("call(public * io.kotest.matchers..should*(..))")
    fun shouldPointcut() {
    }

    @Before("pushCluePointcut()")
    fun onPushClueStart(joinPoint: JoinPoint) {
        if (DISABLED) return

        val clue = (joinPoint.args.firstOrNull() as? DescriptionProvider)?.invoke()
            ?: joinPoint.args.firstOrNull().toString()

        val uuid = UUID.randomUUID().toString()
        activeStepUuid.set(uuid)

        allure.startStep(uuid, StepResult().apply {
            name = "Check object: $clue"
            parameters = listOf(Parameter().setName("object").setValue(clue.take(250)))
        })
    }

    @Before("shouldPointcut()")
    fun onShouldStart(joinPoint: JoinPoint) {
        if (DISABLED) return

        val (actual, expected) = joinPoint.args.firstOrNull() to joinPoint.args.drop(1)
        val uuid = UUID.randomUUID().toString()

        allure.startStep(uuid, StepResult().apply {
            name = "$actual ${joinPoint.signature.name} ${expected.joinToString(", ")}"
            parameters = listOf(
                Parameter().setName("actual").setValue(actual.toString())
            ) + expected.mapIndexed { i, e -> Parameter().setName("$i-expected").setValue(e.toString()) }
        })
    }

    @AfterThrowing(pointcut = "shouldPointcut()", throwing = "exception")
    fun onShouldFail(exception: Throwable) {
        if (DISABLED) return

        updateCurrentStep(Status.BROKEN, exception)
        closeActiveClueStep(exception)
    }

    @AfterReturning(pointcut = "shouldPointcut()")
    fun onShouldSuccess() {
        if (DISABLED) return

        allure.updateStep { it.status = Status.PASSED }
        allure.stopStep()
    }

    @AfterReturning(pointcut = "popCluePointcut()")
    fun onPopClueSuccess() {
        if (DISABLED) return

        activeStepUuid.get()?.let {
            allure.updateStep(it) { it.status = Status.PASSED }
            allure.stopStep(it)
            activeStepUuid.remove()
        }
    }

    private fun updateCurrentStep(status: Status, exception: Throwable?) {
        allure.updateStep {
            it.status = ResultsUtils.getStatus(exception).orElse(status)
            it.statusDetails = ResultsUtils.getStatusDetails(exception).orElse(null)
        }
        allure.stopStep()
    }

    private fun closeActiveClueStep(exception: Throwable?) {
        activeStepUuid.get()?.let {
            allure.updateStep(it) {
                it.status = ResultsUtils.getStatus(exception).orElse(Status.BROKEN)
                it.statusDetails = ResultsUtils.getStatusDetails(exception).orElse(null)
            }
            allure.stopStep(it)
            activeStepUuid.remove()
        }
    }
}
