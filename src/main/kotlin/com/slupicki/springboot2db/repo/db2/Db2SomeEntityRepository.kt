package com.slupicki.springboot2db.repo.db2

import com.slupicki.springboot2db.domain.SomeEntity
import org.springframework.data.repository.CrudRepository

interface Db2SomeEntityRepository : CrudRepository<SomeEntity, String> {
    fun findByNameLike(p: String): List<SomeEntity>
    fun findByNameLikeAndIdLessThanEqual(p: String, id: Long): List<SomeEntity>
}