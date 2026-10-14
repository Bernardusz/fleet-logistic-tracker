package io.github.bernardusz.fleet_logistic_tracker.fleet

import io.github.bernardusz.fleet_logistic_tracker.fleet.dto.FleetCreationRequest
import io.github.bernardusz.fleet_logistic_tracker.fleet.dto.FleetInformationResponse
import io.github.bernardusz.fleet_logistic_tracker.fleet.dto.toResponse
import io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.missing.FleetNotFoundException
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserSecurity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant

@Service
class FleetService(
    private val fleetRepository: FleetRepository
) {
    @Transactional
    suspend fun createFleet(
        fleetCreationRequest: FleetCreationRequest,
    ): Long {
        val fleet: Fleet = Fleet(
            userId = fleetCreationRequest.userId,
            vehicleCode = fleetCreationRequest.vehicleCode,
            licensePlate = fleetCreationRequest.licensePlate,
            model = fleetCreationRequest.model,
        )
        return fleetRepository.save(
            fleet
        ).id
            ?: throw IllegalStateException("Database failed to generate fleet ID")
    }

    @Transactional
    fun findAll(
        identifier: String? = null,
        userId: Long,
        createdAfter: Instant? = null,
        createdBefore: Instant? = null,
    ): Flow<FleetInformationResponse> {
        return fleetRepository.searchFleets(
                userId = userId,
                identifier = identifier,
                createdAfter = createdAfter,
                createdBefore = createdBefore
            ).map { fleet -> fleet.toResponse() }
    }

    @Transactional
    suspend fun findByIdAndUserId(
        id: Long, userId: Long,
    ): FleetInformationResponse {
        val fleet: Fleet = fleetRepository.findByIdAndUserId(
            id = id, userId = userId
        ) ?: throw FleetNotFoundException("Database failed to find Fleet")

        return fleet.toResponse()
    }

    @Transactional
    suspend fun updateFleet(
        id: Long,
        userId: Long,
        fleetCreationRequest: FleetCreationRequest,
    ): FleetInformationResponse {
        val fleet: Fleet = fleetRepository.findByIdAndUserId(id, userId)
        ?: throw FleetNotFoundException("Fleet with ID '$id' not found.")

        val updatedFleet: Fleet = fleet.copy(
            userId = fleetCreationRequest.userId,
            vehicleCode = fleetCreationRequest.vehicleCode,
            licensePlate = fleetCreationRequest.licensePlate,
            model = fleetCreationRequest.model,
        )

        return fleetRepository.save(updatedFleet)
            .toResponse()
    }

    @Transactional
    suspend fun deleteFleet(
        id: Long,
        userId: Long
    ) {
        val deletedCount: Long = fleetRepository
            .deleteByIdAndUserId(
            id, userId = userId
        )

        if (deletedCount == 0L) {
            throw FleetNotFoundException(
                "Database failed to delete Fleet with ID '$id'."
            )
        }
    }





}