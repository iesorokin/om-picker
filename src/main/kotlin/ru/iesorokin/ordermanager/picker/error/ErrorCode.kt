package ru.iesorokin.ordermanager.orchestrator.error

enum class ErrorCode(val code: Int, val errorMessage: String) {
    UNEXPECTED(201, "unexpected.error"),
    INVALID_ATTRIBUTE(103, "invalid.parameter"),
    PICKER_NOT_AVAILABLE(105, "PICKER_NOT_AVAILABLE"),
    PICKED_ERROR(101, "PICKED_ERROR")
}
