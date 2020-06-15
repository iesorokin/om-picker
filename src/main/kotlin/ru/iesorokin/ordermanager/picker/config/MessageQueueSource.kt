package ru.iesorokin.ordermanager.picker.config

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.SubscribableChannel

internal const val START_PICKING = "startPicking"

internal const val PICKED_SUCCESS = "pickedSuccess"

interface MessageQueueSource {
    // Input
    @Input(START_PICKING)
    fun startPicking(): SubscribableChannel

    // Output
    @Output(PICKED_SUCCESS)
    fun pickedSuccess(): SubscribableChannel
}

