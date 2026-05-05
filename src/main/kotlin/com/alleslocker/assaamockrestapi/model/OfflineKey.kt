package com.alleslocker.assaamockrestapi.model

import java.time.LocalDateTime

data class OfflineKey(
    val id: String,
    val issuerUserId: String,
    val operatingDeviceId: String,
    val lockingDeviceId: String,
    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    val issuanceDatetime: LocalDateTime,
    val deliveryMechanism: DeliveryMechanism
)