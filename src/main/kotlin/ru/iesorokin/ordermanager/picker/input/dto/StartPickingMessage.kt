package ru.iesorokin.ordermanager.picker.input.dto

data class StartPickingMessage(
        val pickedId: String,
        val extOrderId: String
)