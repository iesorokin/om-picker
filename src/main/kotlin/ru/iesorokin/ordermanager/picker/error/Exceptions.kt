package ru.iesorokin.ordermanager.orchestrator.error

class PaymentTaskNotFoundException(override val message: String) : RuntimeException()

class SendStartOrchestrationProcessMessageException(override val message: String) : RuntimeException()

class SendCancelProcessMessageException(override val message: String) : RuntimeException()
