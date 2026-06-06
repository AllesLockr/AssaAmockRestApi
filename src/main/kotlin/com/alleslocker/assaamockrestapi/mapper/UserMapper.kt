package com.alleslocker.assaamockrestapi.mapper

import com.alleslocker.assaamockrestapi.dto.CreateUserResponse
import com.alleslocker.assaamockrestapi.model.User
import com.alleslocker.assaamockrestapi.persistence.entity.UserEntity

fun UserEntity.toUser(): User = User(
    id = this.id!!,
    role = this.role
)

fun UserEntity.toCreateUserResponse(): CreateUserResponse = CreateUserResponse(
    id = this.id!!,
    accessKey = this.accessKey,
    role = this.role
)