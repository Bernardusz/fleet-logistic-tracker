package io.github.bernardusz.fleet_logistic_tracker.fleet

import io.github.bernardusz.fleet_logistic_tracker.fleet.dto.FleetCreationRequest
import io.github.bernardusz.fleet_logistic_tracker.fleet.dto.FleetInformationResponse
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserSecurity
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Instant

@RestController
@RequestMapping("/api/v1/fleet")
class FleetController(
    private val fleetService: FleetService
) {
    @PostMapping
    suspend fun createFleet(
        @Valid @RequestBody
        fleetCreationRequest: FleetCreationRequest
    ): ResponseEntity<Void> {
        val fleetId: Long = fleetService
            .createFleet(fleetCreationRequest)

        return ResponseEntity.created(
            URI.create(
                "/api/v1/fleet/$fleetId"
            )
        ).build()
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllFleets(
        @AuthenticationPrincipal userSecurity: UserSecurity,
        @RequestParam(required = false) identifier: String?,
        @RequestParam(required = false) createdBefore: Instant?,
        @RequestParam(required = false) createdAfter: Instant?
    ): Flow<FleetInformationResponse> {
        return fleetService.findAll(
            identifier = identifier,
            userId = userSecurity.getId(),
            createdAfter = createdAfter,
            createdBefore = createdBefore
        )
    }

    @GetMapping("/{fleetId}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getFleet(
        @AuthenticationPrincipal userSecurity: UserSecurity,
        @PathVariable("fleetId") fleetId: Long
    ): FleetInformationResponse {
        return fleetService.findByIdAndUserId(
            id = fleetId, userId = userSecurity.getId()
        )
    }

    @PutMapping("/{fleetId}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun updateFleet(
        @AuthenticationPrincipal userSecurity: UserSecurity,
        @PathVariable("fleetId") fleetId: Long,
        @RequestBody request: FleetCreationRequest
    ): FleetInformationResponse {
        return fleetService.updateFleet(
            id = fleetId, userId = userSecurity.getId(),
            fleetCreationRequest = request
        )
    }

    @DeleteMapping("/{fleetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteFleet(
        @PathVariable("fleetId") fleetId: Long,
        @AuthenticationPrincipal userSecurity: UserSecurity
    ) {
        fleetService.deleteFleet(
            id = fleetId,
            userId = userSecurity.getId(),
        )
    }
}