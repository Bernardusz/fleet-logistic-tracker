package io.github.bernardusz.fleet_logistic_tracker.fleet.dto

import io.github.bernardusz.fleet_logistic_tracker.fleet.Fleet
import java.time.Instant

data class FleetInformationResponse(
    val userId: Long,
    val vehicleCode: String,
    val licensePlate: String,
    val model: String,
    val createdAt: Instant
)

fun Fleet.toResponse(): FleetInformationResponse = FleetInformationResponse(
    userId = userId,
    vehicleCode = vehicleCode,
    licensePlate = licensePlate,
    model = model,
    createdAt = createdAt
)