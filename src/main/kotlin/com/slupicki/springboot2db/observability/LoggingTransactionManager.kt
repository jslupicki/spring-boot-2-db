package com.slupicki.springboot2db.observability

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionStatus
import javax.sql.DataSource

class LoggingTransactionManager(
    dataSource: DataSource
) : DataSourceTransactionManager(dataSource) {

    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun doBegin(transaction: Any, definition: TransactionDefinition) {
        log.info { "------------------ Transaction begin. transaction=$transaction, definition=$definition" }
        super.doBegin(transaction, definition)
    }

    override fun doCommit(status: DefaultTransactionStatus) {
        log.info { "------------------ Transaction commit: ${statusToString(status)}" }
        super.doCommit(status)
    }

    override fun doRollback(status: DefaultTransactionStatus) {
        log.info { "------------------ Transaction rollback: ${statusToString(status)}" }
        super.doRollback(status)
    }

    private fun statusToString(status: DefaultTransactionStatus): String =
        "DefaultTransactionStatus{" +
                "name='" + status.transactionName + '\'' +
                ", newTransaction=" + status.isNewTransaction +
                ", completed=" + status.isCompleted +
                ", rollbackOnly=" + status.isRollbackOnly +
                ", transaction=" + status.transaction +
                ", suspendedResources=" + status.suspendedResources +
                '}'
}