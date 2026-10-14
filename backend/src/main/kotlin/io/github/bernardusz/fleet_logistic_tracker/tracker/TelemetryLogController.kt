package io.github.bernardusz.fleet_logistic_tracker.tracker

import io.github.bernardusz.fleet_logistic_tracker.tracker.dto.TelemetryLogResponse
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserSecurity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/v1/tracker")
class TrackerController(
    private val trackerService: TrackerService
) {

    @GetMapping(path = ["/stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamUserTelemetry(
        @AuthenticationPrincipal userSecurity: UserSecurity
    ): Flow<TelemetryLog> {
        return trackerService.streamTelemetryForUser(userSecurity.getId())
    }

    @GetMapping("/{vehicleCode}")
    fun getAllHistoryOfAVehicle(
        @AuthenticationPrincipal userSecurity: UserSecurity,
        @PathVariable vehicleCode: String
    ): Flow<TelemetryLogResponse> {
        return trackerService.findAllByVehicleCodeAndUserId(
            vehicleCode = vehicleCode,
            userId = userSecurity.getId()
        )
    }
}