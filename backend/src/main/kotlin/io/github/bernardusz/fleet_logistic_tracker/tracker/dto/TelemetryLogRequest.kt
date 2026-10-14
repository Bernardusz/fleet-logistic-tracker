package io.github.bernardusz.fleet_logistic_tracker.tracker.dto

import java.time.Instant

data class TelemetryLogRequest(
    val id: Long,
    val vehicleCode: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val fuelLevel: Double,
    val recordedAt: Instant = Instant.now()
)