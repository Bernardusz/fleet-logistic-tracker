package io.github.bernardusz.fleet_logistic_tracker.tracker

import io.github.bernardusz.fleet_logistic_tracker.fleet.FleetRepository
import io.github.bernardusz.fleet_logistic_tracker.tracker.dto.TelemetryLogResponse
import io.github.bernardusz.fleet_logistic_tracker.tracker.dto.toResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import org.springframework.stereotype.Service

@Service
class TrackerService(
    private val simulationEngine: FleetSimulationEngine,
    private val fleetRepository: FleetRepository,
    private val telemetryLogRepository: TelemetryLogRepository
) {
    fun streamTelemetryForUser(userId: Long): Flow<TelemetryLog> = flow {
        // 1. Execute 1 query when the SSE connection opens to get all owned vehicle codes
        val userVehicleCodes: Set<String> = fleetRepository.findAllByUserId(userId)
            .map { fleet -> fleet.vehicleCode }
            .toSet() // Collect into an in-memory Set

        // 2. Filter the global stream in-memory without hitting the database on every tick
        simulationEngine.streamTelemetry()
            .filter { log -> userVehicleCodes.contains(log.vehicleCode) }
            .collect { log -> emit(log) }
    }

    fun findAllByVehicleCodeAndUserId(
        userId: Long,
        vehicleCode: String
    ): Flow<TelemetryLogResponse> {
        return telemetryLogRepository.findAllByVehicleCodeAndUserId(
            vehicleCode = vehicleCode,
            userId = userId
        ).map { telemetryLog -> telemetryLog.toResponse() }
    }
}