package io.github.bernardusz.fleet_logistic_tracker.tracker

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TelemetryLogRepository : CoroutineCrudRepository<TelemetryLog, Long> {
    @Query("""
        SELECT * FROM telemetry_logs
        WHERE vehicle_code = :vehicleCode
            AND user_id = :userId
        ORDER BY recorded_at DESC
    """)
    fun findAllByVehicleCodeAndUserId(
        userId: Long,
        vehicleCode: String
    ): Flow<TelemetryLog>

    @Query("""
        DELETE FROM telemetry_logs 
        WHERE recorded_at < NOW() - INTERVAL '30 days'
    """)
    suspend fun deleteAllOlderThan30Days(): Long
}