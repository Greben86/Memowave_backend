package dev.greben.memowave.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
class SecurityConfiguration(
    private val authProvider: ExternalJwtAuthProvider
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/login", "/error").permitAll()
                auth.anyRequest().authenticated()
            }
            .formLogin { form ->
                form.defaultSuccessUrl("/admin/upload", true)
            }
            .authenticationProvider(authProvider) // Подключаем нашу логику

        return http.build()
    }
}