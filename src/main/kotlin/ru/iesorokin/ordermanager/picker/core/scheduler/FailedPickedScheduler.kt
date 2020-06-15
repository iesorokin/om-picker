package ru.iesorokin.ordermanager.orchestrator.core.scheduler

import mu.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.picker.core.domain.FailedPickedMessage
import ru.iesorokin.ordermanager.picker.core.repository.FailedPickedMessageRepository
import ru.iesorokin.ordermanager.picker.core.service.conducting.PickerService
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
@RefreshScope
class FailedPickedScheduler(
        private val failedPickedMessageRepository: FailedPickedMessageRepository,
        private val pickerService: PickerService,
        @param:Value("\${schedule.picked.minutesForSelfProcessing}")
        private val minutesForSelfProcessing: Long
) {
    @SchedulerLock(
            name = "picked",
            lockAtLeastFor = "\${schedule.picked.lockAtLeast}",
            lockAtMostFor = "\${schedule.picked.lockAtMost}"
    )
    @Scheduled(cron = "\${schedule.picked.cron}", zone = "UTC")
    fun schedule() {
        val failedMessages = failedPickedMessageRepository.findAll()

        if (failedMessages.isNotEmpty()) {
            notify(failedMessages)

            failedMessages.forEach {
                pickerService.picked(it)
            }
        }
    }

    private fun notify(failedMessages: List<FailedPickedMessage>) {
        log.info { "Start scheduling for picked processes: $failedMessages" }

        val oldMessages = failedMessages.filter {
            it.creationDate.isBefore(LocalDateTime.now().minusMinutes(minutesForSelfProcessing))
        }
        if (oldMessages.isNotEmpty()) {
            log.error { "Some picked can't be processed too long. Messages: $oldMessages" }
        }
    }
}