package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на регистрацию")
data class SignUpRequest(
    @Schema(description = "Имя пользователя", example = "Вася")
    @Size(min = 1, max = 255, message = "Имя пользователя должно содержать от 1 до 255 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    var username: String,

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    var password: String,

    @Schema(description = "Ссылка на фото пользователя", example = "http://...")
    @Size(min = 8, max = 255, message = "Длина ссылки должна быть от 1 до 255 символов")
    @NotBlank(message = "Поле не может быть пустыми")
    var imageUrl: String,

    @Schema(description = "Email пользователя", example = "your_email@example.com")
    @Email(message = "Адрес Email должен быть валидным")
    @NotBlank(message = "Поле не может быть пустым")
    var email: String,

    @Schema(description = "Название сессии", example = "my_session_from_android")
    @Size(min = 10, max = 255, message = "Длина названия сессии должна быть от 10 до 255 символов")
    @NotBlank(message = "Название сессии не может быть пустыми")
    val session: String
)
