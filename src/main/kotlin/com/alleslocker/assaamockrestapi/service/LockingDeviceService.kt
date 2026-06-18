package com.alleslocker.assaamockrestapi.service

import com.alleslocker.assaamockrestapi.mapper.toLockingDevice
import com.alleslocker.assaamockrestapi.model.ClaimingStatus
import com.alleslocker.assaamockrestapi.model.LockingDevice
import com.alleslocker.assaamockrestapi.persistence.entity.LockingDeviceEntity
import com.alleslocker.assaamockrestapi.persistence.repository.LockingDeviceRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LockingDeviceService(private val repository: LockingDeviceRepository) {

    fun getLockingDevices(): List<LockingDevice> =
        repository.findAll().map { it.toLockingDevice() }

    fun createLockingDevice(name: String): LockingDevice {
        val entity = LockingDeviceEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            claimingStatus = ClaimingStatus.UNCLAIMED
        )
        return repository.save(entity).toLockingDevice()
    }

    fun getLockingDeviceById(id: String): LockingDevice? =
        repository.findById(id).orElse(null)?.toLockingDevice()

    fun deleteLockingDevice(id: String) = repository.deleteById(id)
}