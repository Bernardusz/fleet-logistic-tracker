package io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.missing

class UserNotFoundException(
    message: String,
    cause: Throwable? = null,
    code: Int = 404
) : NotFoundException(message, cause, code)