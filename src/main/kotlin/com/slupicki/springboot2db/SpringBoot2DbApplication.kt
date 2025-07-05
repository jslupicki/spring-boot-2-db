package com.slupicki.springboot2db

import com.slupicki.springboot2db.domain.SomeEntity
import com.slupicki.springboot2db.repo.db1.Db1SomeEntityRepository
import com.slupicki.springboot2db.repo.db2.Db2SomeEntityRepository
import com.slupicki.springboot2db.service.TransactionService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(
    exclude = [
        DataSourceAutoConfiguration::class,
        JdbcRepositoriesAutoConfiguration::class,
    ]
)
class SpringBoot2DbApplication {
    companion object {
        val log = KotlinLogging.logger { }
    }

    @Bean
    fun runner(
        db1SomeEntityRepository: Db1SomeEntityRepository,
        db2SomeEntityRepository: Db2SomeEntityRepository,
        transactionService: TransactionService,
    ) = CommandLineRunner { _ ->
        log.info { "Application started" }
        (1..5).forEach { i ->
            val db1Entity = db1SomeEntityRepository.save(SomeEntity(name = "DB1 Entity $i"))
            val db2Entity = db2SomeEntityRepository.save(SomeEntity(name = "DB2 Entity $i"))
            log.info { "$db1Entity saved to DB1" }
            log.info { "$db2Entity saved to DB2" }
        }
        log.info { "DB1 Entities: ${db1SomeEntityRepository.findAll()}" }
        log.info { "DB2 Entities: ${db2SomeEntityRepository.findAll()}" }
        log.info { "DB1 Entities like %3: ${db1SomeEntityRepository.findByNameLike("%3")}" }
        log.info { "DB2 Entities like %2: ${db2SomeEntityRepository.findByNameLike("%2")}" }
        log.info {
            "DB1 Entities like %Entity% and id <= 3 : ${
                db1SomeEntityRepository.findByNameLikeAndIdLessThanEqual(
                    "%Entity%",
                    3
                )
            }"
        }
        log.info {
            "DB2 Entities like %Entity% and id <= 2: ${
                db2SomeEntityRepository.findByNameLikeAndIdLessThanEqual(
                    "%Entity%",
                    2
                )
            }"
        }
        try {
            transactionService.performTransaction(3)
        } catch (e: Exception) {
            log.error { "Got error in transactionService.performTransaction(3)" }
        }
        log.info { "Application ended" }
    }
}

fun main(args: Array<String>) {
    val ctx = runApplication<SpringBoot2DbApplication>(*args)
    ctx.close()
}
