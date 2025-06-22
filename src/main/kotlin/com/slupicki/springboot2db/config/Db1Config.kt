package com.slupicki.springboot2db.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
@EnableJdbcRepositories(
    basePackages = ["com.slupicki.springboot2db.repo.db1"],
    jdbcOperationsRef = "db1JdbcTemplate"
)
class Db1Config {

    @Bean
    @ConfigurationProperties("spring.datasource.db1")
    fun db1DataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    fun db1DataSource(): DataSource {
        return db1DataSourceProperties()
            .initializeDataSourceBuilder()
            .build()
    }

    @Bean
    @Primary
    fun db1JdbcTemplate(db1DataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(db1DataSource)
    }
}