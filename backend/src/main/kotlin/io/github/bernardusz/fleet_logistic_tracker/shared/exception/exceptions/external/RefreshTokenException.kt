package io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.external

class RefreshTokenException(
    message: String,
    cause: Throwable? = null,
    code: Int = 401
) : ExternalException(message, cause, code)