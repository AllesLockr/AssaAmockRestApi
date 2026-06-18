package com.alleslocker.assaamockrestapi.controller

import com.alleslocker.assaamockrestapi.dto.CreatePermissionRequest
import com.alleslocker.assaamockrestapi.dto.CreatePermissionResponse
import com.alleslocker.assaamockrestapi.dto.PageResponse
import com.alleslocker.assaamockrestapi.model.Permission
import com.alleslocker.assaamockrestapi.service.PermissionService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/permission")
class PermissionController(private val service: PermissionService) {

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Permissions listed")
    fun getPermissions(
        @RequestParam(name = "user-id", required = false) userId: String?
    ): PageResponse<Permission> = PageResponse(service.getPermissions(userId))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Permission created"),
            ApiResponse(responseCode = "404", description = "User or locking device not found", content = [])
        ]
    )
    fun createPermission(@RequestBody request: CreatePermissionRequest): CreatePermissionResponse =
        service.createPermission(request)

    @GetMapping("/{permissionId}")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Permission found"),
            ApiResponse(responseCode = "404", description = "Permission not found", content = [])
        ]
    )
    fun getPermission(@PathVariable permissionId: String): ResponseEntity<Permission> {
        val permission = service.getPermissionById(permissionId)
        return if (permission == null) ResponseEntity.notFound().build() else ResponseEntity.ok(permission)
    }

    @DeleteMapping("/{permissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Permission revoked")
    fun deletePermission(@PathVariable permissionId: String) =
        service.deletePermission(permissionId)
}