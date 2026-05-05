package com.alleslocker.assaamockrestapi.model

import java.time.LocalDateTime

data class OperationalCertificate(
    val eligibleForReKeying: Boolean,
    val expirationDate: LocalDateTime,
    val revoked: Boolean
)
