package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Категория слов: ответ")
data class CategoryResponse(
    @Schema(description = "Идентификатор категории", example = "123")
    val id: Long?,
    @Schema(description = "Название категории", example = "Неправильные глаголы")
    var name: String?,
    @Schema(description = "Описание", example = "100 самых популярных непривильных глаголов")
    var description: String?,
    @Schema(description = "Цвет категории", example = "Зеленый")
    var color: String?
)