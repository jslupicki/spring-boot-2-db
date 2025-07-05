package com.slupicki.springboot2db.observability

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class TestRetryService {
    companion object {
        val log = KotlinLogging.logger {}
    }
    
    private val callCounter = AtomicInteger(0)
    
    @Retry(name = "sqlRetry")
    fun methodThatFailsN(n: Int): String {
        val currentCall = callCounter.incrementAndGet()
        log.info { "Attempt #$currentCall" }
        
        when {
            currentCall <= n -> {
                "Simulating failure on attempt #$currentCall of $n".also {
                    log.error { it }
                    throw RuntimeException(it)
                }
            }
            else -> {
                "Success on attempt #$currentCall of $n".also {
                    log.info { it }
                    return it
                }
            }
        }
    }

    fun resetCounter() {
        callCounter.set(0)
        log.info { "Counter reset to 0" }
    }
}
