package io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.internal

class InternalServerException(
    message: String,
    cause: Throwable? = null,
    val code: Int = 500
) : RuntimeException(message, cause)