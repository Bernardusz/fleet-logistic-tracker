package io.github.bernardusz.fleet_logistic_tracker.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank @field:Size(max = 255)
    val username: String,
    @field:NotBlank @field:Size(max = 255)
    val email: String,
    @field:NotBlank @field:Size(max = 255)
    val password: String
)
