package com.slupicki.springboot2db.observability

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicInteger

@Service
class TestTransactionService {
    companion object {
        val log = KotlinLogging.logger {}
    }

    private val callCounter = AtomicInteger(0)

    @Transactional(transactionManager = "db1TransactionManager")
    fun methodThatCommitsSuccessfully(): String {
        log.info { "Executing transaction method that should commits successfully" }

        return "Transaction completed successfully".also {
            log.info { it }
        }
    }

    @Transactional(transactionManager = "db1TransactionManager")
    fun methodThatRollsBack(): String {
        log.info { "Executing transaction method that should rollback" }

        "Transaction should rollback".also {
            log.error { it }
            throw RuntimeException(it)
        }
    }

    @Retry(name = "sqlRetry")
    @Transactional(transactionManager = "db1TransactionManager")
    fun methodThatRollsBackAndRetry(n: Int): String {
        log.info { "Executing transaction method that should rollback ${callCounter.get()} of $n" }

        if (callCounter.incrementAndGet() <= n) {
            "Simulating rollback for retry attempt ${callCounter.get()}".also {
                log.error { it }
                throw RuntimeException(it)
            }
        } else {
            "Transaction completed successfully after ${callCounter.get()} attempts".also {
                log.info { it }
                return it
            }
        }
    }

    @Transactional(transactionManager = "db1TransactionManager", readOnly = true)
    fun readOnlyMethod(): String {
        log.info { "Executing read-only transaction method" }

        return "Read-only transaction completed".also {
            log.info { it }
        }
    }

    fun resetCounter() {
        callCounter.set(0)
        TestRetryService.Companion.log.info { "Counter reset to 0" }
    }
}
