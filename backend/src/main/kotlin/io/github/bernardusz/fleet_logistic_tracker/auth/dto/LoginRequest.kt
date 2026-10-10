package io.github.bernardusz.fleet_logistic_tracker.auth.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    val identifier: String,
    @field:NotBlank
    val password: String
)
