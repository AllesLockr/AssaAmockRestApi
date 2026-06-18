package com.alleslocker.assaamockrestapi.mapper

import com.alleslocker.assaamockrestapi.model.LockingDevice
import com.alleslocker.assaamockrestapi.persistence.entity.LockingDeviceEntity

fun LockingDeviceEntity.toLockingDevice(): LockingDevice = LockingDevice(
    id = this.id!!,
    name = this.name,
    claimingStatus = this.claimingStatus,
    serialNumber = this.serialNumber,
    hardwareModel = this.hardwareModel,
    hardwareVersion = this.hardwareVersion,
    firmwareVersion = this.firmwareVersion
)