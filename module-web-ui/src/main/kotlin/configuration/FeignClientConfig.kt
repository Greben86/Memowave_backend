package dev.greben.memowave.configuration

import dev.greben.memowave.utils.Constants.AUTH_BEARER_PREFIX
import dev.greben.memowave.utils.Constants.AUTH_HEADER_NAME
import feign.RequestInterceptor
import feign.Retryer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder


//@ConditionalOnBean(Authentication::class)
@Configuration
class FeignClientConfig {

    /**
     * Добавление заголовка авторизации
     */
    @Bean
    fun authRequestInterceptor(): RequestInterceptor? {
        return RequestInterceptor { requestTemplate ->
            val headerValue = SecurityContextHolder
                .getContext()
                .authentication
                .principal as String?
            requestTemplate.header(AUTH_HEADER_NAME, "$AUTH_BEARER_PREFIX $headerValue")
//            val jwtToken = (auth as UsernamePasswordAuthenticationToken).principal
//            requestTemplate.header(AUTH_HEADER_NAME, "$AUTH_BEARER_PREFIX ${jwtToken as String}")
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