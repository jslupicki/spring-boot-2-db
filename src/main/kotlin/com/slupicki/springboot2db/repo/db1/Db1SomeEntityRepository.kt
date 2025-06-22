package com.slupicki.springboot2db.repo.db1

import com.slupicki.springboot2db.domain.SomeEntity
import org.springframework.data.repository.CrudRepository

interface Db1SomeEntityRepository : CrudRepository<SomeEntity, String>