package io.github.bernardusz.fleet_logistic_tracker.user

import io.github.bernardusz.fleet_logistic_tracker.shared.utils.createAccessTokenCookie
import io.github.bernardusz.fleet_logistic_tracker.shared.utils.createRefreshTokenCookie
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserInformationResponse
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserSecurity
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserUpdateInformationRequest
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserUpdatePasswordRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PreAuthorize("#userId == authentication.principal.id")
    @GetMapping("/{userId}")
    suspend fun getUserById(
        @PathVariable userId: Long
    ): ResponseEntity<UserInformationResponse> {
        return ResponseEntity.ok(
            userService.getUserById(userId)
        )
    }

    @PreAuthorize("#username == authentication.principal.username")
    @GetMapping("/username/{username}")
    suspend fun getUserByUsername(
        @PathVariable username: String
    ): ResponseEntity<UserInformationResponse> {
        return ResponseEntity.ok(
            userService.getUserByUsername(username)
        )
    }

    @PreAuthorize("#email == authentication.principal.email")
    @GetMapping("/email/{email}")
    suspend fun getUserByEmail(
        @PathVariable email: String
    ): ResponseEntity<UserInformationResponse> {
        return ResponseEntity.ok(
            userService.getUserByEmail(
                email = email
            )
        )
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/information")
    suspend fun updateUserInformation(
        @AuthenticationPrincipal user: UserSecurity,
        @Valid @RequestBody userUpdateInformationRequest: UserUpdateInformationRequest
    ): ResponseEntity<UserInformationResponse> {
        val updatedUser: UserInformationResponse =
        userService.updateUserInformation(
            user.getId(),
            userUpdateInformationRequest
        )

        return ResponseEntity.ok(updatedUser)
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    suspend fun updatePassword(
        @AuthenticationPrincipal user: UserSecurity,
        @Valid @RequestBody userUpdatePasswordRequest: UserUpdatePasswordRequest,
        response: ServerHttpResponse
    ): ResponseEntity<Void> {
        userService.updateUserPassword(
            id = user.getId(),
            userUpdatePasswordRequest = userUpdatePasswordRequest
        )

        val accessTokenCookie: ResponseCookie =
            createAccessTokenCookie("", 0)

        val refreshTokenCookie: ResponseCookie =
            createRefreshTokenCookie("", 0)

        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping
    suspend fun deleteUserById(
        @AuthenticationPrincipal user: UserSecurity,
        response: ServerHttpResponse
    ): ResponseEntity<Void> {
        userService.deleteUser(user.getId())
        val accessToken: ResponseCookie =
            createAccessTokenCookie(
                "",
                0
            )

        val refreshToken: ResponseCookie =
            createRefreshTokenCookie(
                "",
                0
            )

        response.addCookie(accessToken)
        response.addCookie(refreshToken)

        return ResponseEntity.noContent().build()
    }
}