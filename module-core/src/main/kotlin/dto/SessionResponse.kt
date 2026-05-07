package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с сессией")
data class SessionResponse(
    @Schema(description = "Идентификатор сессии", example = "123")
    val sessionId: Long?,

    @Schema(description = "Название сессии", example = "my_session_from_android")
    var name: String?
)
