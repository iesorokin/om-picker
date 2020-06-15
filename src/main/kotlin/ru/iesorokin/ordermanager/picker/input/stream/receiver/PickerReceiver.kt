package ru.iesorokin.ordermanager.picker.input.stream.receiver

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.picker.config.START_PICKING
import ru.iesorokin.ordermanager.picker.core.service.picking.PickerService
import ru.iesorokin.ordermanager.picker.input.dto.StartPickingMessage

private val log = KotlinLogging.logger {}

@Service
class PickerReceiver(
        private val pickerService: PickerService,
        @Value("\${conductor.consumer.default.maxRetry}")
        private val maxRetry: Long
) {
    @StreamListener(START_PICKING)
    fun startPicking(
            @Payload message: StartPickingMessage,
            @Header(name = X_DEATH_HEADER, required = false) death: Map<Any, Any?>?
    ) {
        log.inputMessage(START_PICKING, message)
        if (death.isDeadLetterCountOverflown(maxRetry)) {
            log.deadLetterCountOverflownError(maxRetry, START_PICKING, message)
            return
        }

        pickerService.picked(message)
    }
}
