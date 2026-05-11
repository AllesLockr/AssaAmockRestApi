package com.alleslocker.assaamockrestapi.persistence.repository

import com.alleslocker.assaamockrestapi.persistence.entity.UserEntity
import org.springframework.data.repository.ListCrudRepository

interface UserRepository : ListCrudRepository<UserEntity, String>