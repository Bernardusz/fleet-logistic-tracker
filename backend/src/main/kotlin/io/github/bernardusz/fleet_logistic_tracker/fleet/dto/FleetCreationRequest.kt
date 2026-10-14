package io.github.bernardusz.fleet_logistic_tracker.fleet.dto

data class FleetCreationRequest(
    val userId: Long,
    val vehicleCode: String,
    val licensePlate: String,
    val model: String,
)
