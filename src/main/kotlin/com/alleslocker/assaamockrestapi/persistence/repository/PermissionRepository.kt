package com.alleslocker.assaamockrestapi.persistence.repository

import com.alleslocker.assaamockrestapi.persistence.entity.PermissionEntity
import org.springframework.data.repository.ListCrudRepository

interface PermissionRepository : ListCrudRepository<PermissionEntity, String> {
    fun findByUserId(userId: String): List<PermissionEntity>
}