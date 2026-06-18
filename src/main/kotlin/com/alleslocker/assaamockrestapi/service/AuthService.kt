package com.alleslocker.assaamockrestapi.service

import com.alleslocker.assaamockrestapi.config.MockAuthProperties
import com.alleslocker.assaamockrestapi.dto.LoginRequest
import com.alleslocker.assaamockrestapi.dto.LoginResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class AuthService(private val properties: MockAuthProperties) {

    private val tokens = ConcurrentHashMap.newKeySet<String>()

    fun login(apiKey: String?, request: LoginRequest): LoginResponse {
        if (apiKey != properties.apiKey) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (request.userId != properties.userId || request.accessKey != properties.accessKey) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        val token = UUID.randomUUID().toString()
        tokens.add(token)
        return LoginResponse(token)
    }

    fun isValidToken(token: String): Boolean = tokens.contains(token)
}