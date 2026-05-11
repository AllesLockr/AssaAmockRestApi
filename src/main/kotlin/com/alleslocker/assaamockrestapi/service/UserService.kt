package com.alleslocker.assaamockrestapi.service

import com.alleslocker.assaamockrestapi.mapper.toEntity
import com.alleslocker.assaamockrestapi.mapper.toUser
import com.alleslocker.assaamockrestapi.model.Role
import com.alleslocker.assaamockrestapi.model.User
import com.alleslocker.assaamockrestapi.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUsers(): List<User> {
        return userRepository.findAll().map { userEntity -> userEntity.toUser() }
    }

    fun createUser(role: Role): User {
        val user = User(id = UUID.randomUUID().toString(), role = role)
        println("User created: $user")

        return userRepository.save(user.toEntity()).toUser()
    }

    fun getUserById(userId: String): User? {
        return userRepository.findById(userId).orElse(null)?.toUser()
    }
}