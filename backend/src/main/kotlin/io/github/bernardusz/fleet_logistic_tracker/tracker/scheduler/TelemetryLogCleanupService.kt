package io.github.bernardusz.fleet_logistic_tracker.tracker.scheduler

import io.github.bernardusz.fleet_logistic_tracker.tracker.TelemetryLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TelemetryCleanupService(
    private val telemetryLogRepository: TelemetryLogRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    // Runs every night at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    fun schedule30DayCleanup() {
        CoroutineScope(Dispatchers.IO).launch {
            val deletedCount = telemetryLogRepository.deleteAllOlderThan30Days()
            logger.info("Telemetry cleanup completed: $deletedCount old records purged.")
        }
    }
}