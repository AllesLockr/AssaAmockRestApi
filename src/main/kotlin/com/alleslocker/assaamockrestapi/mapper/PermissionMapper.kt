package com.alleslocker.assaamockrestapi.mapper

import com.alleslocker.assaamockrestapi.model.Permission
import com.alleslocker.assaamockrestapi.persistence.entity.PermissionEntity

fun PermissionEntity.toPermission(): Permission = Permission(
    id = this.id!!,
    userId = this.userId,
    lockingDeviceId = this.lockingDeviceId,
    operationType = this.operationType,
    permissionType = this.permissionType,
    operatingKeyValidityDuration = this.operatingKeyValidityDuration,
    start = this.start,
    end = this.end,
    weekdays = this.weekdays.ifEmpty { null },
    intervals = this.intervals.ifEmpty { null }
)