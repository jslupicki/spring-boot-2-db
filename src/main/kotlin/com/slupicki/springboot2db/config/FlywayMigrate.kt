package com.slupicki.springboot2db.config

import org.flywaydb.core.Flyway
import org.springframework.stereotype.Component

@Component
class FlywayMigrate(
    val db1Flyway: Flyway,
    val db2Flyway: Flyway,
) {

    init {
        db1Flyway.migrate()
        db2Flyway.migrate()
    }
}