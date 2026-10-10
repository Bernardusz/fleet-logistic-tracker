package io.github.bernardusz.fleet_logistic_tracker.auth.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long,
    val refreshTokenExpiresIn: Long,
)
