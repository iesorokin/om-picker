package ru.iesorokin.ordermanager.orchestrator.output.converter

import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class PickedMessageBuilder {
    fun builPickedSuccessMessage(result: String, picked: Collection<Boolean>): Message<*> {
        return             MessageBuilder
                .withPayload(picked)
                .build()
    }

}

