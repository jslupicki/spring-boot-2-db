package com.slupicki.springboot2db

import com.slupicki.springboot2db.domain.SomeEntity
import com.slupicki.springboot2db.repo.db1.Db1SomeEntityRepository
import com.slupicki.springboot2db.repo.db2.Db2SomeEntityRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(
    exclude = [
        DataSourceAutoConfiguration::class,
        JdbcRepositoriesAutoConfiguration::class,
    ]
)
class SpringBoot2DbApplication {
    companion object {
        val log = LoggerFactory.getLogger(SpringBoot2DbApplication::class.java)
    }

    @Bean
    fun runner(
        db1SomeEntityRepository: Db1SomeEntityRepository,
        db2SomeEntityRepository: Db2SomeEntityRepository,
    ) = CommandLineRunner { _ ->
        log.info("Application started")
        (1..5).forEach { i ->
            db1SomeEntityRepository.save(
                SomeEntity(
                    id = null,
                    name = "DB1 Entity $i"
                )
            )
            db2SomeEntityRepository.save(
                SomeEntity(
                    name = "DB2 Entity $i"
                )
            )
        }
        log.info("DB1 Entities: ${db1SomeEntityRepository.findAll()}")
        log.info("DB2 Entities: ${db2SomeEntityRepository.findAll()}")
        log.info("Application ended")
    }
}

fun main(args: Array<String>) {
    val ctx = runApplication<SpringBoot2DbApplication>(*args)
    ctx.close()
}
