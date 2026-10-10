package io.github.bernardusz.fleet_logistic_tracker.user.dto

data class UserCreateRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "ROLE_USER",
)