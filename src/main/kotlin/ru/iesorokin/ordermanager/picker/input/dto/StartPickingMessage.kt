package ru.iesorokin.ordermanager.orchestrator.input.dto

data class StartPickingMessage(
        val pickedId: String,
        val extOrderId: String
)