package dev.greben.memowave.configuration

import dev.greben.memowave.utils.Constants.AUTH_BEARER_PREFIX
import dev.greben.memowave.utils.Constants.AUTH_HEADER_NAME
import feign.RequestInterceptor
import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContextHolder

@Configuration
class FeignClientConfig {

    /**
     * Добавление заголовка авторизации
     */
    @Bean
    fun authRequestInterceptor(): RequestInterceptor? {
        return RequestInterceptor { requestTemplate ->
            val tokenValue = SecurityContextHolder.getContext()
                ?.authentication
                ?.principal as String?
            if (tokenValue != null && tokenValue.isNotEmpty()) {
                requestTemplate.header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + tokenValue)
            }
        }
    }

    /**
     * Повторение запросов в случае ошибки
     */
    @Bean
    fun retryer(): Retryer? {
        return Retryer.Default(100, 2000, 3)
    }
}