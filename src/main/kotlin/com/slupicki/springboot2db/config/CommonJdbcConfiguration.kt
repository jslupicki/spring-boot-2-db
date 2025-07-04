package com.slupicki.springboot2db.config

import org.springframework.data.convert.CustomConversions
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.core.mapping.JdbcSimpleTypes
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

class CommonJdbcConfiguration : AbstractJdbcConfiguration() {

    fun createConversions(dataSource: DataSource, converters: List<Any> = emptyList()): JdbcCustomConversions =
        createConversions(NamedParameterJdbcTemplate(dataSource), converters)

    fun createConversions(operations: NamedParameterJdbcOperations, converters: List<Any> = emptyList()): JdbcCustomConversions {
        val dialect = jdbcDialect(operations)
        val simpleTypeHolder = if (dialect.simpleTypes().isEmpty())
            JdbcSimpleTypes.HOLDER
        else
            SimpleTypeHolder(dialect.simpleTypes(), JdbcSimpleTypes.HOLDER)

        val converters: List<Any> = ArrayList<Any>().apply {
            addAll(dialect.converters)
            addAll(JdbcCustomConversions.storeConverters())
            addAll(converters)
        }

        return JdbcCustomConversions(
            CustomConversions.StoreConversions.of(
                simpleTypeHolder,
                converters
            ),
            userConverters()
        )
    }


}