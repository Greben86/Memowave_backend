package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Пользователь")
data class UserResponse(
    @Schema(description = "Идентификатор пользователя", example = "123")
    val id: Long?,
    @Schema(description = "Имя пользователя", example = "Вася")
    val username: String?,
    @Schema(description = "Ссылка на фото пользователя", example = "http://...")
    val imageUrl: String?,
    @Schema(description = "Email пользователя", example = "your_email@example.com")
    val email: String?
)
