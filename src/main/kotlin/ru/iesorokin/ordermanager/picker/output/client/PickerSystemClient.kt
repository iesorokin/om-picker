package ru.iesorokin.ordermanager.picker.output.client

import org.apache.http.client.utils.URIBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.iesorokin.ordermanager.picker.error.PaymentTaskNotFoundException

private const val PAYMENT_TASKS_URL_PARAMETER_EXT_ORDER_ID = "extOrderId"

@Component
class PickerSystemClient(
        private val restTemplatePicker: RestTemplate,
        @Value("\${pick.task.urlTasksByParams}")
        private val urlPaymentTasksByParameters: String
) {

    fun pick(
            extOrderId: String
    ): List<Boolean> {
        val uri = URIBuilder(urlPaymentTasksByParameters)
                .addParameter(PAYMENT_TASKS_URL_PARAMETER_EXT_ORDER_ID, extOrderId)
                .build()

        val pickResponse = restTemplatePicker.exchange(
                uri,
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Boolean>>() {}
        ).body
        if (pickResponse.isNullOrEmpty()) throw PaymentTaskNotFoundException("Not success for extOrderId: $extOrderId")

        return pickResponse.map { true }
    }

}