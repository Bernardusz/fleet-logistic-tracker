package io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.existed

class UserAlreadyExistsException(
    message: String,
    cause: Throwable? = null,
    code: Int = 409
): AlreadyExistsException(message, cause, code)