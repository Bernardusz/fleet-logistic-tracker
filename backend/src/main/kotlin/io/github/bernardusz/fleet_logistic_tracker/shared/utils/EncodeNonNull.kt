package io.github.bernardusz.fleet_logistic_tracker.shared.utils

import org.springframework.security.crypto.password.PasswordEncoder

fun PasswordEncoder.encodeNonNull(rawPassword: CharSequence): String {
    return requireNotNull(this.encode(rawPassword)) {
        "PasswordEncoder returned null for the provided Password"
    }
}