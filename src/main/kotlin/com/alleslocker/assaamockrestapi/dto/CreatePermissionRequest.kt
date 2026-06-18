package com.alleslocker.assaamockrestapi.dto

import com.alleslocker.assaamockrestapi.model.OperationType
import com.alleslocker.assaamockrestapi.model.PermissionType
import java.time.DayOfWeek
import java.time.Instant

data class CreatePermissionRequest(
    val userId: String,
    val lockingDeviceId: String,
    val operationType: OperationType,
    val permissionType: PermissionType,
    val start: Instant,
    val end: Instant,
    val operatingKeyValidityDuration: String = "P8D",
    val weekdays: Set<DayOfWeek>? = null,
    val intervals: Set<String>? = null
)