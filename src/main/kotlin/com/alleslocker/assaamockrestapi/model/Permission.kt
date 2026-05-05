package com.alleslocker.assaamockrestapi.model

import java.time.LocalDateTime

data class Permission(
    val id: String,
    val userId: String,
    val lockingDeviceId: String,
    val operationType: OperationType,
    val permissionType: PermissionType,
    val operatingKeyValidityDuration: String,
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null,
    val weekdays: Set<Int>,
    val intervals: Set<String>
)