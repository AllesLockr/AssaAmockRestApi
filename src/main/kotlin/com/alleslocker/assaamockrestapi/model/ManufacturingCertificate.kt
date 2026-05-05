package com.alleslocker.assaamockrestapi.model

import java.time.LocalDateTime

data class ManufacturingCertificate(
    val eligibleForReKeying: Boolean,
    val expirationDate: LocalDateTime,
    val revoked: Boolean
)
