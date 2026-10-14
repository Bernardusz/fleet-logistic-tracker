package io.github.bernardusz.fleet_logistic_tracker.fleet

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("fleet")
data class Fleet(
    @Id val id: Long? = null,
    val userId: Long,
    val vehicleCode: String,
    val licensePlate: String,
    val model: String,
    val createdAt: Instant = Instant.now(),
)