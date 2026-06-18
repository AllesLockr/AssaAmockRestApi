package com.alleslocker.assaamockrestapi.controller

import com.alleslocker.assaamockrestapi.dto.LoginRequest
import com.alleslocker.assaamockrestapi.dto.LoginResponse
import com.alleslocker.assaamockrestapi.service.AuthService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
@SecurityRequirement(name = "apiKey")
class LoginController(private val authService: AuthService) {

    @PostMapping
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Token issued"),
            ApiResponse(responseCode = "401", description = "Invalid credentials", content = []),
            ApiResponse(responseCode = "403", description = "Invalid API key", content = [])
        ]
    )
    fun login(
        @RequestHeader(name = "X-Api-Key", required = false) apiKey: String?,
        @RequestBody request: LoginRequest
    ): LoginResponse = authService.login(apiKey, request)
}