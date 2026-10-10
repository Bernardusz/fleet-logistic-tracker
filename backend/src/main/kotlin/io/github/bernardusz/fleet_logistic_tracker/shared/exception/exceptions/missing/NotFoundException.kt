package io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.missing

open class NotFoundException(
    message: String,
    cause: Throwable? = null,
    val code: Int
) : RuntimeException(message, cause)