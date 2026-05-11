package com.alleslocker.assaamockrestapi.mapper

import com.alleslocker.assaamockrestapi.model.User
import com.alleslocker.assaamockrestapi.persistence.entity.UserEntity

fun UserEntity.toUser(): User {
    return User(
        role = this.role,
        id = this.id!!
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        role = this.role
    )
}