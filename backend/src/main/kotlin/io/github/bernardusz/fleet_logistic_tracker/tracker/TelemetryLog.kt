package io.github.bernardusz.fleet_logistic_tracker.tracker

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("telemetry_logs")
data class TelemetryLog(
    @Id val id: Long? = null,
    val vehicleCode: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val fuelLevel: Double,
    val recordedAt: Instant = Instant.now()
)