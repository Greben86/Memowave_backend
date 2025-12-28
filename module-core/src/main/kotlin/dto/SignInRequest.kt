package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на аутентификацию")
data class SignInRequest(
    @Schema(description = "Имя пользователя", example = "Вася")
    @Size(min = 1, max = 255, message = "Имя пользователя должно содержать от 1 до 255 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    val username: String,

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    val password: String
)
