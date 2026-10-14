package io.github.bernardusz.fleet_logistic_tracker.tracker.dto

import io.github.bernardusz.fleet_logistic_tracker.tracker.TelemetryLog
import java.time.Instant

data class TelemetryLogResponse(
    val id: Long,
    val vehicleCode: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val fuelLevel: Double,
    val recordedAt: Instant = Instant.now()
)

fun TelemetryLog.toResponse(): TelemetryLogResponse = TelemetryLogResponse(
    id = id ?: throw IllegalArgumentException("id is null"),
    vehicleCode = vehicleCode,
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    fuelLevel = fuelLevel,
    recordedAt = recordedAt
)