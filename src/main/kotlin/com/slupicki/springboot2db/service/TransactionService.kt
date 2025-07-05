package com.slupicki.springboot2db.service

import com.slupicki.springboot2db.repo.db1.Db1SomeEntityRepository
import com.slupicki.springboot2db.repo.db2.Db2SomeEntityRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    val db1SomeEntityRepository: Db1SomeEntityRepository,
    val db2SomeEntityRepository: Db2SomeEntityRepository,
) {
    companion object {
        val log = KotlinLogging.logger { }
    }

    @Retry(name = "sqlRetry")
    @Transactional(transactionManager = "db1TransactionManager")
    fun performTransaction(throwAfter: Int = -1) {
        log.info { "Starting transaction" }
        try {
            val db1Entities = db1SomeEntityRepository.findAll()
            db1Entities.forEachIndexed { idx, se ->
                log.info { "Processing DB1 Entity: $se" }
                se.copy(name = "Updated DB1 Entity ${se.id}")
                    .let { db1SomeEntityRepository.save(it) }
                if (idx + 1 == throwAfter) {
                    log.error { "Simulating failure after processing $throwAfter entities" }
                    throw Exception("Simulated failure")
                }
            }
            db1SomeEntityRepository.findAll().forEach {
                log.info { "DB1 Entity after update: $it" }
            }
            log.info { "Transaction completed successfully" }
        } catch (e: Exception) {
            log.error(e) { "Transaction failed, rolling back" }
            throw e
        }
    }
}