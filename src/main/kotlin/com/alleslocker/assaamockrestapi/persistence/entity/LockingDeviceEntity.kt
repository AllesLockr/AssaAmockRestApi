package com.alleslocker.assaamockrestapi.persistence.entity

import com.alleslocker.assaamockrestapi.model.ClaimingStatus
import com.alleslocker.assaamockrestapi.model.HardwareModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "locking_devices")
class LockingDeviceEntity(
    @Id
    var id: String? = null,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var claimingStatus: ClaimingStatus,

    var serialNumber: String? = null,

    @Enumerated(EnumType.STRING)
    var hardwareModel: HardwareModel? = null,

    var hardwareVersion: String? = null,

    var firmwareVersion: String? = null
)