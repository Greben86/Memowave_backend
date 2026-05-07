package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Категория слов: ответ")
data class CategoryResponse(
    @Schema(description = "Идентификатор категории", example = "123")
    val id: Long?,
    @Schema(description = "Название категории", example = "Неправильные глаголы")
    var name: String?,
    @Schema(description = "Описание", example = "100 самых популярных непривильных глаголов")
    var description: String?,
    @Schema(description = "Цвет категории", example = "#FF00FF00")
    var color: String?,
    @Schema(description = "Название иконки", example = "default")
    var iconName: String?,
    @Schema(description = "Идентификатор пользователя", example = "0")
    var userId: Long = 0L,
    @Schema(description = "Дата добавления категории", example = "2026-03-13T10:00:00")
    var createdAt: LocalDateTime?,
    @Schema(description = "Дата обновления категории", example = "2026-03-13T10:00:00")
    var updatedAt: LocalDateTime?
)