package com.alleslocker.assaamockrestapi.web

import com.alleslocker.assaamockrestapi.service.AuthService
import jakarta.servlet.DispatcherType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(private val authService: AuthService) : HandlerInterceptor {

    private val publicPaths = listOf("/login", "/swagger-ui", "/v3/api-docs", "/h2-console")

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (request.dispatcherType != DispatcherType.REQUEST) {
            return true
        }
        val path = request.requestURI.removePrefix(request.contextPath)
        if (publicPaths.any { path.startsWith(it) }) {
            return true
        }
        val header = request.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
        val token = header.removePrefix("Bearer ").trim()
        if (!authService.isValidToken(token)) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
        return true
    }
}