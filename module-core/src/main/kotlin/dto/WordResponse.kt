package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Слово - перевод")
data class WordResponse(
    @Schema(description = "Слово", example = "example")
    private var text: String?,
    @Schema(description = "Перевод", example = "пример")
    private var translate: String?,
    @Schema(description = "Пример", example = "It is example")
    private var example: String?,
    @Schema(description = "Ссылка на изображение", example = "http://...")
    private var imageUrl: String?
)
