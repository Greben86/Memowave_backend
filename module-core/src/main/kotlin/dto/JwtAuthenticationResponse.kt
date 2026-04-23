package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ c токеном доступа")
data class JwtAuthenticationResponse(
    @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    var token: String
)
