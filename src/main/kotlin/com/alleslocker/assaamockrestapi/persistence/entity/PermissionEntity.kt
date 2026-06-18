package com.alleslocker.assaamockrestapi.persistence.entity

import com.alleslocker.assaamockrestapi.model.OperationType
import com.alleslocker.assaamockrestapi.model.PermissionType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.DayOfWeek
import java.time.Instant

@Entity
@Table(name = "permissions")
class PermissionEntity(
    @Id
    var id: String? = null,

    @Column(nullable = false)
    var userId: String,

    @Column(nullable = false)
    var lockingDeviceId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var operationType: OperationType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var permissionType: PermissionType,

    @Column(nullable = false)
    var operatingKeyValidityDuration: String,

    @Column(name = "start_datetime", nullable = false)
    var start: Instant,

    @Column(name = "end_datetime", nullable = false)
    var end: Instant,

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "permission_weekdays", joinColumns = [JoinColumn(name = "permission_id")])
    var weekdays: MutableSet<DayOfWeek> = mutableSetOf(),

    @ElementCollection
    @CollectionTable(name = "permission_intervals", joinColumns = [JoinColumn(name = "permission_id")])
    var intervals: MutableSet<String> = mutableSetOf()
)