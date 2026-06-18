package com.alleslocker.assaamockrestapi.controller

import com.alleslocker.assaamockrestapi.dto.CreateUserDto
import com.alleslocker.assaamockrestapi.dto.CreateUserResponse
import com.alleslocker.assaamockrestapi.dto.PageResponse
import com.alleslocker.assaamockrestapi.model.User
import com.alleslocker.assaamockrestapi.service.UserService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val service: UserService) {

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Users listed")
    fun getUsers(): PageResponse<User> = PageResponse(service.getUsers())

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "User created")
    fun createUser(@RequestBody request: CreateUserDto): CreateUserResponse =
        service.createUser(request.role)

    @GetMapping("/{userId}")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User found"),
            ApiResponse(responseCode = "404", description = "User not found", content = [])
        ]
    )
    fun getUser(@PathVariable userId: String): ResponseEntity<User> {
        val user = service.getUserById(userId)
        return if (user == null) ResponseEntity.notFound().build() else ResponseEntity.ok(user)
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "User deleted")
    fun deleteUser(@PathVariable userId: String) = service.deleteUser(userId)
}