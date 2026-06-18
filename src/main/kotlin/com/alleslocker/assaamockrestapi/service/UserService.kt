package com.alleslocker.assaamockrestapi.service

import com.alleslocker.assaamockrestapi.dto.CreateUserResponse
import com.alleslocker.assaamockrestapi.mapper.toCreateUserResponse
import com.alleslocker.assaamockrestapi.mapper.toUser
import com.alleslocker.assaamockrestapi.model.Role
import com.alleslocker.assaamockrestapi.model.User
import com.alleslocker.assaamockrestapi.persistence.entity.UserEntity
import com.alleslocker.assaamockrestapi.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository) {

    fun getUsers(): List<User> = userRepository.findAll().map { it.toUser() }

    fun createUser(role: Role): CreateUserResponse {
        val entity = UserEntity(
            id = UUID.randomUUID().toString(),
            role = role,
            accessKey = UUID.randomUUID().toString()
        )
        return userRepository.save(entity).toCreateUserResponse()
    }

    fun getUserById(userId: String): User? =
        userRepository.findById(userId).orElse(null)?.toUser()

    fun deleteUser(userId: String) = userRepository.deleteById(userId)
}