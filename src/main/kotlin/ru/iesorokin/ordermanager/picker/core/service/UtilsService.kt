package ru.iesorokin.ordermanager.picker.core.service

import org.springframework.stereotype.Service
import java.util.*

@Service
class UtilsService {
    fun randomUUID(): String {
        return UUID.randomUUID().toString()
    }
}