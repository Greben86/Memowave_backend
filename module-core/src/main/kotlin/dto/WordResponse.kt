package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Слово - перевод: ответ")
data class WordResponse(
    @Schema(description = "Категория", example = "Не правильные глаголы")
    var category: String,
    @Schema(description = "Слово", example = "example")
    var text: String,
    @Schema(description = "Перевод", example = "пример")
    var translate: String,
    @Schema(description = "Пример", example = "It is example")
    var example: String,
    @Schema(description = "Ссылка на изображение", example = "http://...")
    var imageUrl: String?
)
