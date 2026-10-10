package io.github.bernardusz.fleet_logistic_tracker.user.dto

data class UserUpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
