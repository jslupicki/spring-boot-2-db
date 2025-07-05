package com.slupicki.springboot2db.observability

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.core.EventConsumer
import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.event.RetryEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetryEventLogger() {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Bean
    fun myRetryRegistryEventConsumer(): RegistryEventConsumer<Retry> =
        object : RegistryEventConsumer<Retry> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<Retry>) {
                entryAddedEvent
                    .getAddedEntry()
                    .eventPublisher
                    .onEvent { e -> log.info { e.toString() } }
            }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<Retry>) {
            }

            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<Retry>) {
            }
        }

}