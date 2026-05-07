package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Запрос на смену пароля")
data class ChangePasswordRequest(
    @Schema(description = "Текущий пароль", example = "current_password")
    @NotBlank(message = "Текущий пароль не может быть пустыми")
    val currentPassword: String,

    @Schema(description = "Новый пароль", example = "new_password")
    @NotBlank(message = "Новый пароль не может быть пустыми")
    val newPassword: String
)