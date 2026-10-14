package io.github.bernardusz.fleet_logistic_tracker.fleet

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface FleetRepository : CoroutineCrudRepository<Fleet, Long> {
    @Query("""
        SELECT * FROM fleet
        WHERE user_id = :userId
          AND (
            :identifier IS NULL OR :identifier = '' OR (
              vehicle_code ILIKE CONCAT('%', :identifier, '%') OR
              license_plate ILIKE CONCAT('%', :identifier, '%') OR
              model ILIKE CONCAT('%', :identifier, '%')
            )
          )
          AND (:createdBefore::timestamptz IS NULL OR created_at <= :createdBefore)
          AND (:createdAfter::timestamptz IS NULL OR created_at >= :createdAfter)
    """)
    fun searchFleets(
        @Param("userId") userId: Long,
        @Param("identifier") identifier: String? = null,
        @Param("createdBefore") createdBefore: Instant? = null,
        @Param("createdAfter") createdAfter: Instant? = null
    ): Flow<Fleet>

    suspend fun findByIdAndUserId(
        @Param("id") id: Long,
        @Param("userId") userId: Long,
    ): Fleet?

    suspend fun deleteByIdAndUserId(
        @Param("id") id: Long,
        @Param("userId") userId: Long
    ): Long

    suspend fun existsByVehicleCodeAndUserId(
        vehicleCode: String,
        userId: Long
    ): Boolean

    fun findAllByUserId(
        @Param("userId") userId: Long,
    ): Flow<Fleet>
}