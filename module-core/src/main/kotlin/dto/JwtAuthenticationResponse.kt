package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ c токенами доступа")
data class JwtAuthenticationResponse(
    @Schema(description = "Access Token токен для доступа к защищённым ресурсам", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    var accessToken: String,
    @Schema(description = "Refresh Token токен для получения нового Access Token, когда старый истёк", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    var refreshToken: String,
    @Schema(description = "Название сессии", example = "123_test_123_insomnia")
    var session: String
)
