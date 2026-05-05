package com.alleslocker.assaamockrestapi.model

import java.time.LocalDateTime

data class GatewayDevice(
    val id: String,
    val serialNumber: String,
    val activationStatus: ActivationStatus,
    val creationDate: LocalDateTime,

    val activationDatetime: LocalDateTime? = null,
    val hardwareModel: String? = null,
    val hardwareVersion: String? = null,
    val firmwareVersion: String? = null,
)
