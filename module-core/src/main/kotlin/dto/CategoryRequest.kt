package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Категория слов: запрос")
data class CategoryRequest(
    @Schema(description = "Название категории", example = "Неправильные глаголы")
    @Size(min = 1, max = 255, message = "Длина пароля должна быть от 1 до 255 символов")
    @NotBlank(message = "Поле не может быть пустым")
    var name: String,
    @Schema(description = "Описание", example = "100 самых популярных непривильных глаголов")
    @Size(min = 1, max = 255, message = "Длина пароля должна быть от 1 до 255 символов")
    @NotBlank(message = "Поле не может быть пустым")
    var description: String,
    @Schema(description = "Цвет категории", example = "Зеленый")
    @Size(min = 1, max = 255, message = "Длина пароля должна быть от 1 до 255 символов")
    @NotBlank(message = "Поле не может быть пустым")
    var color: String,
    @Schema(description = "Идентификатор пользователя", example = "0")
    var userId: Long = 0L
)