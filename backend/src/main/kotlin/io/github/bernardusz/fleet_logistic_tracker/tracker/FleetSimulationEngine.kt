package io.github.bernardusz.fleet_logistic_tracker.tracker

import io.github.bernardusz.fleet_logistic_tracker.fleet.FleetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.random.Random

@Service
class FleetSimulationEngine(
    private val fleetRepository: FleetRepository,
    private val telemetryLogRepository: TelemetryLogRepository
) {

    /**
     * Generates a real-time telemetry stream for all registered vehicles.
     * Ticks every 1000ms (1 second).
     */
    fun streamTelemetry(): Flow<TelemetryLog> = flow {
        var tickCount = 0
        while (true) {
            val shouldSaveToDb = (tickCount % 30 == 0)
            fleetRepository.findAll().collect { vehicle ->
                var log = TelemetryLog(
                    vehicleCode = vehicle.vehicleCode,
                    // Simulate random jitter around Surabaya base coordinates (-7.2504, 112.7688)
                    latitude = -7.2504 + (Random.nextDouble() - 0.5) * 0.02,
                    longitude = 112.7688 + (Random.nextDouble() - 0.5) * 0.02,
                    speed = Random.nextDouble(20.0, 90.0),
                    fuelLevel = Random.nextDouble(10.0, 100.0),
                    recordedAt = Instant.now()
                )

                if (shouldSaveToDb) {
                    log = telemetryLogRepository.save(log)
                }

                // Emit to the active Flow stream
                emit(log)
            }

            // Non-blocking Coroutine delay (1 second interval)
            tickCount++
            delay(1000)
        }
    }
}