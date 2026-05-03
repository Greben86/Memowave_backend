package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Пользователь")
data class UserResponse(
    @Schema(description = "Идентификатор пользователя", example = "123")
    val id: Long?,
    @Schema(description = "Имя пользователя", example = "Вася")
    @Size(min = 1, max = 255, message = "Имя должно содержать от 1 до 255 символов")
    @NotBlank(message = "Имя не может быть пустыми")
    val username: String?,
    @Schema(description = "Ссылка на фото пользователя", example = "http://...")
    @Size(max = 255, message = "Ссылка должна содержать не более 255 символов")
    val imageUrl: String?,
    @Schema(description = "Email пользователя", example = "your_email@example.com")
    @Size(max = 255, message = "Email не должен содержать более 255 символов")
    @NotBlank(message = "Email не может быть пустыми")
    @Email
    val email: String?,
    @Schema(description = "Опыт пользователя", example = "3")
    val experience: Long?
)
