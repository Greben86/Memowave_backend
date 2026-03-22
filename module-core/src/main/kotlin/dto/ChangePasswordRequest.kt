package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Запрос на смену пароля")
data class ChangePasswordRequest(
    @Schema(description = "Текущий пароль", example = "current_password")
    val currentPassword: String,

    @Schema(description = "Новый пароль", example = "new_password")
    val newPassword: String
)