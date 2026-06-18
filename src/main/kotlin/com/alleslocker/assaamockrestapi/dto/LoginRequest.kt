package com.alleslocker.assaamockrestapi.dto

data class LoginRequest(
    val userId: String,
    val accessKey: String
)