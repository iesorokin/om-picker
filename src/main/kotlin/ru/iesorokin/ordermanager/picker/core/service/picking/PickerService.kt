package ru.iesorokin.ordermanager.picker.core.service.picking

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.picker.core.domain.FailedPickedMessage
import ru.iesorokin.ordermanager.picker.core.repository.FailedPickedMessageRepository
import ru.iesorokin.ordermanager.picker.core.service.PickerSystemService
import ru.iesorokin.ordermanager.picker.input.dto.StartPickingMessage
import ru.iesorokin.ordermanager.picker.output.stream.sender.OrchestratorSender
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class PickerService(
        private val orchestratorSender: OrchestratorSender,
        private val pickerSystemService: PickerSystemService,
        private val failedPickedMessageRepository: FailedPickedMessageRepository
) {
    fun picked(message: StartPickingMessage) {
        val paymentTaskIds = pickerSystemService.pick(listOf())
        if (paymentTaskIds != null) {
            orchestratorSender.sendPickedSuccess(
                    result = message.pickedId, picked = paymentTaskIds
            )
        } else {
            log.warn { "Not all picked task was created for message : $message" }
            failedPickedMessageRepository.save(FailedPickedMessage(
                    pickedId = message.pickedId, extOrderIds =  listOf(), creationDate = LocalDateTime.now()
            ))
        }
    }

    fun picked(message: FailedPickedMessage) {
        val paymentTaskIds = pickerSystemService.pick(message.extOrderIds)
        if (paymentTaskIds != null) {
            orchestratorSender.sendPickedSuccess(
                    result = message.pickedId, picked = paymentTaskIds
            )
            failedPickedMessageRepository.delete(message)
        } else {
            log.warn { "Not all piked tasks was created for message : $message" }
        }
    }
}
