package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Запрос для получения нового Access Token")
data class JwtRefreshRequest(
    @Schema(description = "Refresh Token токен для получения нового Access Token, когда старый истёк", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    @NotBlank(message = "Refresh Token не может быть пустыми")
    var refreshToken: String
)