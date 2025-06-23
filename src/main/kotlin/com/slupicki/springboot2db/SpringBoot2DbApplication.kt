package com.slupicki.springboot2db

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class SpringBoot2DbApplication

fun main(args: Array<String>) {
    runApplication<SpringBoot2DbApplication>(*args)
}
