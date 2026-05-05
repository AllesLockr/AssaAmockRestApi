package com.alleslocker.assaamockrestapi.model

data class LockingDevice(
    val id: String,
    val name: String,
    val claimingStatus: ClaimingStatus,

    val operationalCertificate: OperationalCertificate? = null,
    val manufacturingCertificate: ManufacturingCertificate? = null,
    val serialNumber: String? = null,
    val hardwareModel: HardwareModel? = null,
    val hardwareVersion: String? = null,
    val firmwareVersion: String? = null,
)