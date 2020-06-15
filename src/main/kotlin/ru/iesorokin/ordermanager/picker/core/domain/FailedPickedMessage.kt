package ru.iesorokin.ordermanager.orchestrator.core.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class FailedPickedMessage(
        @Id
        val pickedId: String,
        val extOrderIds: Collection<String>,
        val creationDate: LocalDateTime
)