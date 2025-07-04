package com.slupicki.springboot2db.config

import com.slupicki.springboot2db.repo.db2.Db2SomeEntityRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.convert.DataAccessStrategy
import org.springframework.data.jdbc.core.convert.IdGeneratingEntityCallback
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.core.convert.RelationResolver
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory
import org.springframework.data.relational.RelationalManagedTypes
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.data.relational.core.mapping.DefaultNamingStrategy
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.Optional

@Configuration
class Db2JDBCRepositoryConfig(
    val db2JdbcTemplate: NamedParameterJdbcTemplate,
) : CommonJdbcConfiguration() {
    @Bean
    fun db2NamingStrategy(): NamingStrategy = DefaultNamingStrategy.INSTANCE

    @Bean("db2JdbcMappingContext")
    override fun jdbcMappingContext(
        @Qualifier("db2NamingStrategy")
        namingStrategy: Optional<NamingStrategy>,
        @Qualifier("db2JdbcCustomConversions")
        customConversions: JdbcCustomConversions,
        @Qualifier("db2JdbcManagedTypes")
        jdbcManagedTypes: RelationalManagedTypes,
    ): JdbcMappingContext {
        val mappingContext = JdbcMappingContext(namingStrategy.orElse(DefaultNamingStrategy.INSTANCE))
        mappingContext.setSimpleTypeHolder(customConversions.simpleTypeHolder)
        mappingContext.setManagedTypes(jdbcManagedTypes)
        return mappingContext
    }

    @Bean("db2JdbcCustomConversions")
    override fun jdbcCustomConversions(
    ): JdbcCustomConversions = createConversions(db2JdbcTemplate)

    @Bean("db2JdbcManagedTypes")
    override fun jdbcManagedTypes(): RelationalManagedTypes = super.jdbcManagedTypes()

    @Bean("db2IdGeneratingBeforeSaveCallback")
    override fun idGeneratingBeforeSaveCallback(
        @Qualifier("db2JdbcMappingContext")
        mappingContext: JdbcMappingContext,
        @Qualifier("db2JdbcTemplate")
        operations: NamedParameterJdbcOperations,
        @Qualifier("db2JdbcDialect")
        dialect: Dialect,
    ): IdGeneratingEntityCallback = super.idGeneratingBeforeSaveCallback(mappingContext, operations, dialect)

    @Bean("db2JdbcDialect")
    override fun jdbcDialect(
        @Qualifier("db2JdbcTemplate")
        operations: NamedParameterJdbcOperations,
    ): Dialect = super.jdbcDialect(operations)

    @Bean("db2JdbcConverter")
    override fun jdbcConverter(
        @Qualifier("db2JdbcMappingContext")
        mappingContext: JdbcMappingContext,
        @Qualifier("db2JdbcTemplate")
        operations: NamedParameterJdbcOperations,
        @Qualifier("db2DataAccessStrategyBean")
        @Lazy relationResolver: RelationResolver,
        @Qualifier("db2JdbcCustomConversions")
        conversions: JdbcCustomConversions,
        @Qualifier("db2JdbcDialect")
        dialect: Dialect,
    ): JdbcConverter = super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)

    @Bean("db2DataAccessStrategyBean")
    override fun dataAccessStrategyBean(
        @Qualifier("db2JdbcTemplate")
        operations: NamedParameterJdbcOperations,
        @Qualifier("db2JdbcConverter")
        jdbcConverter: JdbcConverter,
        @Qualifier("db2JdbcMappingContext")
        context: JdbcMappingContext,
        @Qualifier("db2JdbcDialect")
        dialect: Dialect,
    ): DataAccessStrategy = super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)

    @Bean("db2JdbcAggregateTemplate")
    override fun jdbcAggregateTemplate(
        applicationContext: ApplicationContext,
        @Qualifier("db2JdbcMappingContext")
        mappingContext: JdbcMappingContext,
        @Qualifier("db2JdbcConverter")
        converter: JdbcConverter,
        @Qualifier("db2DataAccessStrategyBean")
        dataAccessStrategy: DataAccessStrategy,
    ): JdbcAggregateTemplate =
        super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy)

    @Bean
    fun db2JdbcRepositoryFactory(
        @Qualifier("db2DataAccessStrategyBean")
        dataAccessStrategy: DataAccessStrategy,
        @Qualifier("db2JdbcMappingContext")
        context: JdbcMappingContext,
        @Qualifier("db2JdbcConverter")
        converter: JdbcConverter,
        @Qualifier("db2JdbcDialect")
        dialect: Dialect,
        publisher: ApplicationEventPublisher,
        @Qualifier("db2JdbcTemplate")
        operations: NamedParameterJdbcOperations,
    ): JdbcRepositoryFactory = JdbcRepositoryFactory(
        dataAccessStrategy,
        context,
        converter,
        dialect,
        publisher,
        operations
    )

    @Bean
    fun db2SomeEntityRepository(
        @Qualifier("db2JdbcRepositoryFactory")
        factory: JdbcRepositoryFactory,
    ): Db2SomeEntityRepository = factory.getRepository(Db2SomeEntityRepository::class.java)

}