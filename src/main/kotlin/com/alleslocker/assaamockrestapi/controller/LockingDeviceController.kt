package com.alleslocker.assaamockrestapi.controller

import com.alleslocker.assaamockrestapi.dto.CreateLockingDeviceRequest
import com.alleslocker.assaamockrestapi.dto.PageResponse
import com.alleslocker.assaamockrestapi.model.LockingDevice
import com.alleslocker.assaamockrestapi.service.LockingDeviceService
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
@RequestMapping("/locking-device")
class LockingDeviceController(private val service: LockingDeviceService) {

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Locking devices listed")
    fun getLockingDevices(): PageResponse<LockingDevice> =
        PageResponse(service.getLockingDevices())

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Locking device created")
    fun createLockingDevice(@RequestBody request: CreateLockingDeviceRequest): LockingDevice =
        service.createLockingDevice(request.name)

    @GetMapping("/{lockingDeviceId}")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Locking device found"),
            ApiResponse(responseCode = "404", description = "Locking device not found", content = [])
        ]
    )
    fun getLockingDevice(@PathVariable lockingDeviceId: String): ResponseEntity<LockingDevice> {
        val device = service.getLockingDeviceById(lockingDeviceId)
        return if (device == null) ResponseEntity.notFound().build() else ResponseEntity.ok(device)
    }

    @DeleteMapping("/{lockingDeviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Locking device deleted")
    fun deleteLockingDevice(@PathVariable lockingDeviceId: String) =
        service.deleteLockingDevice(lockingDeviceId)
}