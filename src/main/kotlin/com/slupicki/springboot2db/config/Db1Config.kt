package com.slupicki.springboot2db.config

import com.slupicki.springboot2db.observability.LoggingTransactionManager
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class Db1Config {
    @Bean
    @ConfigurationProperties("db1")
    fun db1DataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun db1DataSource(): DataSource =
        db1DataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()

    @Bean
    fun db1JdbcTemplate(db1DataSource: DataSource): NamedParameterJdbcTemplate =
        NamedParameterJdbcTemplate(db1DataSource)

    @Bean
    fun db1TransactionManager(db1DataSource: DataSource): DataSourceTransactionManager =
        LoggingTransactionManager(db1DataSource)
}