package com.alleslocker.assaamockrestapi.service

import com.alleslocker.assaamockrestapi.dto.CreatePermissionRequest
import com.alleslocker.assaamockrestapi.dto.CreatePermissionResponse
import com.alleslocker.assaamockrestapi.mapper.toPermission
import com.alleslocker.assaamockrestapi.model.Permission
import com.alleslocker.assaamockrestapi.persistence.entity.PermissionEntity
import com.alleslocker.assaamockrestapi.persistence.repository.LockingDeviceRepository
import com.alleslocker.assaamockrestapi.persistence.repository.PermissionRepository
import com.alleslocker.assaamockrestapi.persistence.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository,
    private val userRepository: UserRepository,
    private val lockingDeviceRepository: LockingDeviceRepository
) {

    fun createPermission(request: CreatePermissionRequest): CreatePermissionResponse {
        if (!userRepository.existsById(request.userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }
        if (!lockingDeviceRepository.existsById(request.lockingDeviceId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Locking device not found")
        }
        val entity = PermissionEntity(
            id = UUID.randomUUID().toString(),
            userId = request.userId,
            lockingDeviceId = request.lockingDeviceId,
            operationType = request.operationType,
            permissionType = request.permissionType,
            operatingKeyValidityDuration = request.operatingKeyValidityDuration,
            start = request.start,
            end = request.end,
            weekdays = request.weekdays?.toMutableSet() ?: mutableSetOf(),
            intervals = request.intervals?.toMutableSet() ?: mutableSetOf()
        )
        return CreatePermissionResponse(permissionRepository.save(entity).id!!)
    }

    fun getPermissions(userId: String?): List<Permission> =
        if (userId == null) {
            permissionRepository.findAll().map { it.toPermission() }
        } else {
            permissionRepository.findByUserId(userId).map { it.toPermission() }
        }

    fun getPermissionById(id: String): Permission? =
        permissionRepository.findById(id).orElse(null)?.toPermission()

    fun deletePermission(id: String) = permissionRepository.deleteById(id)
}