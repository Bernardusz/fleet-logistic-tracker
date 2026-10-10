package io.github.bernardusz.fleet_logistic_tracker.shared.exception.exceptions.external

open class ExternalException(
    message: String,
    cause: Throwable? = null,
    val code: Int
) : RuntimeException(message, cause)