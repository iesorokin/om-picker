package ru.iesorokin.ordermanager.picker.core.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.iesorokin.ordermanager.picker.core.domain.FailedPickedMessage

interface FailedPickedMessageRepository : MongoRepository<FailedPickedMessage, String>