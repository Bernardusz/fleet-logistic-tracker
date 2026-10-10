package io.github.bernardusz.fleet_logistic_tracker.auth.dto

import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserInformationResponse

data class UserSessionResponse(
    val isAuthenticated: Boolean,
    val user: UserInformationResponse? = null
)
