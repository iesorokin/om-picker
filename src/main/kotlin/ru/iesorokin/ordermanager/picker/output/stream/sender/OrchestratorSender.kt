package ru.iesorokin.ordermanager.orchestrator.output.stream.sender

import mu.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.picker.config.MessageQueueSource
import ru.iesorokin.ordermanager.picker.error.SendStartOrchestrationProcessMessageException
import ru.iesorokin.ordermanager.picker.output.converter.PickedMessageBuilder
import ru.iesorokin.ordermanager.picker.output.stream.sender.constants.ROUTE_TO

private val log = KotlinLogging.logger {}

@Service
class OrchestratorSender(
        private val messageQueueSource: MessageQueueSource,
        private val pickedMessageBuilder: PickedMessageBuilder
){

    /**
     * @throws SendStartOrchestrationProcessMessageException describing the problem while sending unified message
     */
    fun sendPickedSuccess(result: String, picked: Collection<Boolean>) {
        sendStartProcessMessage(
                pickedMessageBuilder.builPickedSuccessMessage(result, picked)
        )
    }

    private fun sendStartProcessMessage(message: Message<*>) {
        messageQueueSource.pickedSuccess().sendOrThrow(message) { e ->
            SendStartOrchestrationProcessMessageException("Message: $message. Exception: $e")
        }
        log.info { "Message was sent to pickedSuccess exchange. $message" }
    }
}