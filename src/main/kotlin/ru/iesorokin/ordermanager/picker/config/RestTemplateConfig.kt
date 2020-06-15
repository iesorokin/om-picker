package ru.iesorokin.ordermanager.picker.config

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.httpclient.LogbookHttpRequestInterceptor
import org.zalando.logbook.httpclient.LogbookHttpResponseInterceptor
import ru.iesorokin.ordermanager.picker.error.ErrorCode
import ru.iesorokin.ordermanager.picker.error.ErrorCode.PICKED_ERROR
import ru.iesorokin.ordermanager.picker.error.ErrorCode.PICKER_NOT_AVAILABLE
import ru.iesorokin.ordermanager.picker.output.handler.ClientErrorHandler
import ru.iesorokin.ordermanager.picker.output.handler.ClientResponseErrorException
import java.net.URI

@Configuration
class RestTemplateConfig(
        private val logbook: Logbook,
        @Value("\${internalSystem.default.maxConnPerRoute:25}")
        private val maxConnPerRoute: Int,
        @Value("\${internalSystem.default.maxConnTotal:50}")
        private val maxConnTotal: Int
) {

    @Bean
    @LoadBalanced
    fun restTemplatePickerSystem(
            restTemplateBuilder: RestTemplateBuilder,
            @Value("\${pickerSystem.readTimeout:\${internalSystem.default.readTimeout}}")
            readTimeout: Int,
            @Value("\${pickerSystem.connectTimeout:\${internalSystem.default.connectTimeout}}")
            connectTimeout: Int
    ): RestTemplate =
            restTemplateBuilder
                    .requestFactory { createRequestFactory(readTimeout, connectTimeout) }
                    .errorHandler(responseErrorHandlerPickerSystem())
                    .build(CommonRestTemplate::class.java).apply {
                        this.errorCode = ErrorCode.PICKER_NOT_AVAILABLE
                    }

    @Bean
    fun responseErrorHandlerPickerSystem(): ResponseErrorHandler =
            ClientErrorHandler(
                    PICKED_ERROR, PICKER_NOT_AVAILABLE, PICKER_NOT_AVAILABLE
            )

    internal fun createRequestFactory(readTimeout: Int, connectTimeout: Int): ClientHttpRequestFactory =
            HttpComponentsClientHttpRequestFactory(createHttpClient()).also {
                it.setConnectTimeout(connectTimeout)
                it.setReadTimeout(readTimeout)
            }

    private fun createHttpClient(): HttpClient =
            HttpClientBuilder.create()
                    .addInterceptorFirst(LogbookHttpRequestInterceptor(logbook))
                    .addInterceptorLast(LogbookHttpResponseInterceptor())
                    .setMaxConnPerRoute(maxConnPerRoute)
                    .setMaxConnTotal(maxConnTotal)
                    .build()
}

class CommonRestTemplate : RestTemplate() {
    var errorCode: ErrorCode? = null

    override fun <T> doExecute(url: URI, method: HttpMethod?, requestCallback: RequestCallback?, responseExtractor: ResponseExtractor<T>?): T? {
        try {
            return super.doExecute(url, method, requestCallback, responseExtractor)
        } catch (e: ResourceAccessException) {
            throw ClientResponseErrorException(errorCode = errorCode!!)
        }

    }
}