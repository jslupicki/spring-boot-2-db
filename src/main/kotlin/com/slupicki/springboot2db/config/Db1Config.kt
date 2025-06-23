package com.slupicki.springboot2db.config

import com.slupicki.springboot2db.repo.db1.Db1SomeEntityRepository
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.data.convert.CustomConversions
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.convert.DataAccessStrategy
import org.springframework.data.jdbc.core.convert.IdGeneratingEntityCallback
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.core.convert.RelationResolver
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.jdbc.core.mapping.JdbcSimpleTypes
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.relational.RelationalManagedTypes
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.data.relational.core.mapping.DefaultNamingStrategy
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import java.util.Optional
import javax.sql.DataSource

@Configuration
class Db1Config : AbstractJdbcConfiguration() {

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
        DataSourceTransactionManager(db1DataSource)

    @Bean
    fun db1NamingStrategy(): NamingStrategy = DefaultNamingStrategy.INSTANCE

    @Bean("db1JdbcMappingContext")
    override fun jdbcMappingContext(
        @Qualifier("db1NamingStrategy")
        namingStrategy: Optional<NamingStrategy>,
        @Qualifier("db1JdbcCustomConversions")
        customConversions: JdbcCustomConversions,
        @Qualifier("db1JdbcManagedTypes")
        jdbcManagedTypes: RelationalManagedTypes,
    ): JdbcMappingContext {
        val mappingContext = JdbcMappingContext(namingStrategy.orElse(DefaultNamingStrategy.INSTANCE))
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder())
        mappingContext.setManagedTypes(jdbcManagedTypes)
        return mappingContext
    }

    @Bean("db1JdbcCustomConversions")
    override fun jdbcCustomConversions(
    ): JdbcCustomConversions {
        val dialect = jdbcDialect(NamedParameterJdbcTemplate(db1DataSource()))
        val simpleTypeHolder = if (dialect.simpleTypes().isEmpty())
            JdbcSimpleTypes.HOLDER
        else
            SimpleTypeHolder(dialect.simpleTypes(), JdbcSimpleTypes.HOLDER)

        val converters: MutableList<Any?> = ArrayList<Any?>()
        converters.addAll(dialect.getConverters())
        converters.addAll(JdbcCustomConversions.storeConverters())

        return JdbcCustomConversions(
            CustomConversions.StoreConversions.of(
                simpleTypeHolder,
                converters
            ),
            userConverters()
        )
    }

    @Bean("db1JdbcManagedTypes")
    override fun jdbcManagedTypes(): RelationalManagedTypes = super.jdbcManagedTypes()

    @Bean("db1IdGeneratingBeforeSaveCallback")
    override fun idGeneratingBeforeSaveCallback(
        @Qualifier("db1JdbcMappingContext")
        mappingContext: JdbcMappingContext,
        @Qualifier("db1JdbcTemplate")
        operations: NamedParameterJdbcOperations,
        @Qualifier("db1JdbcDialect")
        dialect: Dialect,
    ): IdGeneratingEntityCallback = super.idGeneratingBeforeSaveCallback(mappingContext, operations, dialect)

    @Bean("db1JdbcDialect")
    override fun jdbcDialect(
        @Qualifier("db1JdbcTemplate")
        operations: NamedParameterJdbcOperations,
    ): Dialect = super.jdbcDialect(operations)

    @Bean("db1JdbcConverter")
    override fun jdbcConverter(
        @Qualifier("db1JdbcMappingContext")
        mappingContext: JdbcMappingContext,
        @Qualifier("db1JdbcTemplate")
        operations: NamedParameterJdbcOperations,
        @Qualifier("db1DataAccessStrategyBean")
        @Lazy relationResolver: RelationResolver,
        @Qualifier("db1JdbcCustomConversions")
        conversions: JdbcCustomConversions,
        @Qualifier("db1JdbcDialect")
        dialect: Dialect,
    ): JdbcConverter = super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)

    @Bean("db1DataAccessStrategyBean")
    override fun dataAccessStrategyBean(
        @Qualifier("db1JdbcTemplate")
        operations: NamedParameterJdbcOperations,
        @Qualifier("db1JdbcConverter")
        jdbcConverter: JdbcConverter,
        @Qualifier("db1JdbcMappingContext")
        context: JdbcMappingContext,
        @Qualifier("db1JdbcDialect")
        dialect: Dialect,
    ): DataAccessStrategy = super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)

    @Bean("db1JdbcAggregateTemplate")
    override fun jdbcAggregateTemplate(
        applicationContext: ApplicationContext,
        @Qualifier("db1JdbcMappingContext")
        mappingContext: JdbcMappingContext,
        @Qualifier("db1JdbcConverter")
        converter: JdbcConverter,
        @Qualifier("db1DataAccessStrategyBean")
        dataAccessStrategy: DataAccessStrategy,
    ): JdbcAggregateTemplate =
        super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy)

    @Bean
    fun db1JdbcRepositoryFactory(
        @Qualifier("db1DataAccessStrategyBean")
        dataAccessStrategy: DataAccessStrategy,
        @Qualifier("db1JdbcMappingContext")
        context: JdbcMappingContext,
        @Qualifier("db1JdbcConverter")
        converter: JdbcConverter,
        @Qualifier("db1JdbcDialect")
        dialect: Dialect,
        publisher: ApplicationEventPublisher,
        @Qualifier("db1JdbcTemplate")
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
    fun db1SomeEntityRepository(
        @Qualifier("db1JdbcRepositoryFactory")
        factory: JdbcRepositoryFactory,
    ): Db1SomeEntityRepository = factory.getRepository(Db1SomeEntityRepository::class.java)

}