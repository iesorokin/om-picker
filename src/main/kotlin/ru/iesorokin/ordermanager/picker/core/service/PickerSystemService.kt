package ru.iesorokin.ordermanager.orchestrator.core.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.picker.output.client.PickerSystemClient

private val log = KotlinLogging.logger {}

@Service
class PickerSystemService(
        private val pickerSystemClient: PickerSystemClient
) {
    fun pick(extOrderIds: Collection<String>): Collection<Boolean>? =
            try {
                extOrderIds
                        .map { extOrderId ->
                            pickerSystemClient.pick(extOrderId = extOrderId)
                        }.flatten()
                        .map { it }
            } catch (e: Exception) {
                log.warn { "Error while getting payment tasks for extOrderIds: $extOrderIds. Exception: $e" }
                null
            }

}