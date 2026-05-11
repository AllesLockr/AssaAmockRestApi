package com.alleslocker.assaamockrestapi.controller

import com.alleslocker.assaamockrestapi.dto.CreateUserDto
import com.alleslocker.assaamockrestapi.model.Role
import com.alleslocker.assaamockrestapi.model.User
import com.alleslocker.assaamockrestapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val service: UserService) {

    @GetMapping
    fun getUsers(): List<User> {
        return service.getUsers()
    }

    @PostMapping
    fun createUser(@RequestBody dto: CreateUserDto): User {
        return service.createUser(dto.role)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): ResponseEntity<User> {
        val user = service.getUserById(userId)

        return if (user == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(user)
        }
    }
}