package com.slupicki.springboot2db.observability

import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import kotlin.test.Test

@SpringBootTest
@ExtendWith(OutputCaptureExtension::class)
class LoggingTransactionManagerTest {
   companion object {
        val log = KotlinLogging.logger {}
        const val LOG_START_MARKER = "------------------ Start test ------------------"
    }

    @Autowired
    private lateinit var testTransactionService: TestTransactionService

    @BeforeEach
    fun setUp() {
        testTransactionService.resetCounter()
        log.info { LOG_START_MARKER }
    }

    @Test
    fun `test transaction service method that commits successfully`(output: CapturedOutput) {
        testTransactionService.methodThatCommitsSuccessfully()

        val outputLines = interestingLines(output)

        assertThat(outputLines).anyMatch {
            it.contains("Transaction begin") &&
            it.contains("INFO")
        }

        assertThat(outputLines).anyMatch {
            it.contains("Transaction commit") &&
            it.contains("INFO")
        }
    }

    @Test
    fun `test transaction service method that rolls back`(output: CapturedOutput) {
        try {
            testTransactionService.methodThatRollsBack()
        } catch (e: RuntimeException) {
        }

        val outputLines = interestingLines(output)

        assertThat(outputLines).anyMatch {
            it.contains("Transaction begin") &&
            it.contains("INFO")
        }

        assertThat(outputLines).anyMatch {
            it.contains("Transaction rollback") &&
            it.contains("INFO")
        }
    }

    @Test
    fun `test read-only transaction method`(output: CapturedOutput) {
        testTransactionService.readOnlyMethod()

        val outputLines = interestingLines(output)

        assertThat(outputLines).anyMatch {
            it.contains("Transaction begin") &&
            it.contains("definition=PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly;") &&
            it.contains("INFO")
        }

        assertThat(outputLines).anyMatch {
            it.contains("Transaction commit") &&
            it.contains("INFO")
        }
    }

    @org.junit.jupiter.api.Test
    fun `test method that roll-back and retry n times`(output: CapturedOutput) {
        val n = 2
        val result = testTransactionService.methodThatRollsBackAndRetry(n)

        assertThat(result).isEqualTo("Transaction completed successfully after ${n + 1} attempts")

        val outputLines = output.out.lines()
            .dropWhile { !it.contains(LOG_START_MARKER) }
            .drop(1)
        .filter {
            it.contains("TestTransactionService") ||
            it.contains("LoggingTransactionManager") ||
            it.contains("RetryEventLogger")
        }

        (1..n).forEach { attempt ->
            assertThat(outputLines).anyMatch {
                it.contains("Retry 'sqlRetry'") &&
                it.contains("until attempt '$attempt'") &&
                it.contains("Simulating rollback for retry attempt $attempt")
            }
        }

        assertThat(outputLines).anyMatch {
            it.contains("Retry 'sqlRetry' recorded a successful retry attempt.") &&
            it.contains("Number of retry attempts: '$n'") &&
            it.contains("Simulating rollback for retry attempt $n")
        }

        val beginTransactions = outputLines.count { it.contains("Transaction begin") && it.contains("INFO") }
        val rollbackTransactions = outputLines.count { it.contains("Transaction rollback") && it.contains("INFO") }
        val commitTransactions = outputLines.count { it.contains("Transaction commit") && it.contains("INFO") }

        assertThat(beginTransactions).isEqualTo(n + 1)
        assertThat(rollbackTransactions).isEqualTo(n)
        assertThat(commitTransactions).isEqualTo(1)
    }

    private fun interestingLines(output: CapturedOutput): List<String> = output.out.lines()
            .dropWhile { !it.contains(LOG_START_MARKER) }
            .drop(1)
        .filter {
            it.contains("TestTransactionService") ||
            it.contains("LoggingTransactionManager") ||
            it.contains("Transaction")
        }

}