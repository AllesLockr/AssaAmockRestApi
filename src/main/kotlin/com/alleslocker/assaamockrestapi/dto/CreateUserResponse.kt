package com.alleslocker.assaamockrestapi.dto

import com.alleslocker.assaamockrestapi.model.Role

data class CreateUserResponse(
    val id: String,
    val accessKey: String,
    val role: Role
)