package com.alleslocker.assaamockrestapi.model

import java.time.LocalDateTime

data class GatewayDeviceAssociation(
    val id: String,
    val gatewayDeviceId: String,
    val lockingDeviceId: String,
    val creationDate: LocalDateTime,
)
