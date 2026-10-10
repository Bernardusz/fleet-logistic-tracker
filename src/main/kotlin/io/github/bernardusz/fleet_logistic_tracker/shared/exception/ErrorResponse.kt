package io.github.bernardusz.fleet_logistic_tracker.shared.exception

import java.time.Instant

data class ErrorResponse(
    val message: String,
    val code: Int,
    val timestamp: Instant = Instant.now(),
)
