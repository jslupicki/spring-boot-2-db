package com.slupicki.springboot2db.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("SOME_ENTITY")
data class SomeEntity(
    @Id
    val id: String,
    @Column("NAME")
    val name: String,
)