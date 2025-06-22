package com.slupicki.springboot2db.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig {

    @Bean
    fun db1Flyway(
        @Qualifier("db1DataSource") db1DataSource: DataSource
    ): Flyway {
        return Flyway.configure()
            .dataSource(db1DataSource)
            .locations("classpath:db/migration/db1")
            .load()
    }

    @Bean
    fun db2Flyway(
        @Qualifier("db2DataSource") db2DataSource: DataSource
    ): Flyway {
        return Flyway.configure()
            .dataSource(db2DataSource)
            .locations("classpath:db/migration/db2")
            .load()
    }
}