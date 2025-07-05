package com.slupicki.springboot2db.observability

import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension

@SpringBootTest
@ExtendWith(OutputCaptureExtension::class)
class RetryEventLoggerTest {
   companion object {
        val log = KotlinLogging.logger {}
        const val LOG_START_MARKER = "------------------ Start test ------------------"
    }

    @Autowired
    private lateinit var testRetryService: TestRetryService

    @BeforeEach
    fun setUp() {
        testRetryService.resetCounter()
        log.info { LOG_START_MARKER }
    }

    @Test
    fun `test retry service method that fails N times`(output: CapturedOutput) {
        val n = 2
        testRetryService.methodThatFailsN(n)

        val outputLines = output.out.lines()
            .dropWhile { !it.contains(LoggingTransactionManagerTest.Companion.LOG_START_MARKER) }
            .drop(1)
            .filter {
                it.contains("TestRetryService") ||
                it.contains("RetryEventLogger")
            }

        (1..n).forEach { attempt ->
            assertThat(outputLines).anyMatch {
                it.contains("Simulating failure on attempt #$attempt")
            }
            assertThat(outputLines).anyMatch {
                it.contains("Retry 'sqlRetry'") &&
                it.contains("until attempt '$attempt'") &&
                it.contains("Simulating failure on attempt #$attempt") &&
                it.contains("INFO")
            }
        }

        assertThat(outputLines).anyMatch {
            it.contains("Success on attempt #${n + 1}") &&
            it.contains("INFO")
        }
        assertThat(outputLines).anyMatch {
            it.contains("Retry 'sqlRetry' recorded a successful retry attempt. Number of retry attempts: '$n'") &&
            it.contains("Simulating failure on attempt #$n") &&
            it.contains("INFO")
        }
    }
}