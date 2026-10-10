package io.github.bernardusz.fleet_logistic_tracker.auth

import io.github.bernardusz.fleet_logistic_tracker.auth.dto.LoginRequest
import io.github.bernardusz.fleet_logistic_tracker.auth.dto.LoginResponse
import io.github.bernardusz.fleet_logistic_tracker.auth.dto.RegisterRequest
import io.github.bernardusz.fleet_logistic_tracker.auth.service.JwtService
import io.github.bernardusz.fleet_logistic_tracker.auth.service.RefreshTokenService
import io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.internal.InternalServerException
import io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.missing.UserNotFoundException
import io.github.bernardusz.fleet_logistic_tracker.shared.utils.encodeNonNull
import io.github.bernardusz.fleet_logistic_tracker.user.User
import io.github.bernardusz.fleet_logistic_tracker.user.UserRepository
import io.github.bernardusz.fleet_logistic_tracker.user.dto.UserSecurity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService
) {
    @Transactional
    suspend fun registerUser(registerRequest: RegisterRequest): Long {
        val hashedPassword = passwordEncoder.encode(registerRequest.password)
            ?: throw InternalServerException(
                "Failed to register user - Password Hashing Error."
            )

        val newUser = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = hashedPassword,
        )

        return userRepository.save(newUser).id
            ?: throw IllegalStateException("Database failed to generate user ID")
    }

    @Transactional
    suspend fun loginUser(loginRequest: LoginRequest): LoginResponse{
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.identifier,
                loginRequest.password
            )
        ).awaitSingle()

        val userSecurity = authentication.principal as UserSecurity
        val user = userSecurity.user
        val userId = userSecurity.getId()

        val newAccessToken = jwtService.generateToken(UserSecurity(user))
        val refreshToken: String = refreshTokenService.createRefreshToken(userId)

        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken,
            accessTokenExpiresIn = jwtService.getAccessTokenExpiration(),
            refreshTokenExpiresIn = refreshTokenService.getRefreshTokenExpiration()
        )
    }

    @Transactional
    suspend fun refreshAccessToken(refreshToken: String): LoginResponse{
        val refreshTokenObject: RefreshToken =
            refreshTokenService.findValidToken(
            refreshToken
        )

        val user: User = userRepository.findById(refreshTokenObject.userId)
            ?: throw UserNotFoundException("User with id ${refreshTokenObject.userId} not found")
        val userId = user.id ?: throw UserNotFoundException(
            "User with id ${refreshTokenObject.userId} not found"
        )

        val newAccessToken = jwtService.generateToken(UserSecurity(user))
        val newRefreshToken: String = refreshTokenService.createRefreshToken(userId)
        refreshTokenService.revokeToken(refreshToken)

        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            accessTokenExpiresIn = jwtService.getAccessTokenExpiration(),
            refreshTokenExpiresIn = refreshTokenService.getRefreshTokenExpiration()
        )
    }

    fun getUserTokens(userId: Long): Flow<RefreshToken> {
        return refreshTokenService.getUserTokens(userId)
    }

    @Transactional
    suspend fun revokeAllUserSessions(userId: Long): Int {
        return refreshTokenService.revokeAllUserSessions(userId)
    }
}