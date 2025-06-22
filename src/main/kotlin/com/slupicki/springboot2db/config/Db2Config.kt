package com.slupicki.springboot2db.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
@EnableJdbcRepositories(
    basePackages = ["com.slupicki.springboot2db.repo.db2"],
    jdbcOperationsRef = "db2JdbcTemplate"
)
class Db2Config {

    @Bean
    @ConfigurationProperties("spring.datasource.db2")
    fun db2DataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun db2DataSource(): DataSource {
        return db2DataSourceProperties()
            .initializeDataSourceBuilder()
            .build()
    }

    @Bean
    fun db2JdbcTemplate(db2DataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(db2DataSource)
    }
}