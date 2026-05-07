package dev.greben.memowave.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private var jwtAuthenticationFilterForUserId: JwtAuthenticationFilterForUserId,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { it.disable() }
        // Своего рода отключение CORS (разрешение запросов со всех доменов)
        http.cors { it.configurationSource {
                    val corsConfiguration = CorsConfiguration()
                    corsConfiguration.allowedOriginPatterns = mutableListOf("*")
                    corsConfiguration.allowedMethods = mutableListOf(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                    )
                    corsConfiguration.allowedHeaders = mutableListOf("*")
                    corsConfiguration.allowCredentials = true
                    corsConfiguration
                }
            }
        // Настройка доступа к конечным точкам
        http.authorizeHttpRequests { it
                // Можно указать конкретный путь
                // * - 1 уровень вложенности
                // ** - любое количество уровней вложенности
                .requestMatchers("api/words", "api/words/**").authenticated()
                .requestMatchers("api/categories", "api/categories/**").authenticated()
                .anyRequest().permitAll()
        }
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.addFilterBefore(jwtAuthenticationFilterForUserId, UsernamePasswordAuthenticationFilter::class.java)
        http.headers {
            it.frameOptions(Customizer.withDefaults()).disable()
        }

        return http.build()
    }

    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
            .components(Components()
                .addSecuritySchemes("jwt-token",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT")
                        .`in`(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description("Введите JWT-токен в формате **Bearer &lt;token&gt;**")
                )
            )
}