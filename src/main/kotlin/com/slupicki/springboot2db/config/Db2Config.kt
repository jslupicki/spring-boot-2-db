package com.slupicki.springboot2db.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class Db2Config {
    @Bean
    @ConfigurationProperties("db2")
    fun db2DataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun db2DataSource(): DataSource =
        db2DataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()

    @Bean
    fun db2JdbcTemplate(db2DataSource: DataSource): NamedParameterJdbcTemplate =
        NamedParameterJdbcTemplate(db2DataSource)

    @Bean
    fun db2TransactionManager(db2DataSource: DataSource): DataSourceTransactionManager =
        DataSourceTransactionManager(db2DataSource)
}