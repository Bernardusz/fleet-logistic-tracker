package io.github.bernardusz.fleet_logistic_tracker.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserInformationResponse
import io.swagger.v3.oas.annotations.media.Schema

data class UserSessionResponse(
    @get:JsonProperty("isAuthenticated")
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    val isAuthenticated: Boolean,
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    val user: UserInformationResponse? = null
)
