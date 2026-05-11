package com.alleslocker.assaamockrestapi.persistence.entity

import com.alleslocker.assaamockrestapi.model.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    var id: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role
)