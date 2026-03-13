package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Слово - перевод: запрос")
data class WordRequest(
    @Schema(description = "Идентификатор категории", example = "123")
    val categoryId: Long?,
    @Schema(description = "Слово", example = "example")
    val text: String?,
    @Schema(description = "Перевод", example = "пример")
    val translate: String?,
    @Schema(description = "Пример", example = "It is example")
    val example: String?,
    @Schema(description = "Ссылка на изображение", example = "http://...")
    val imageUrl: String?,
    @Schema(description = "Количество повторений", example = "0")
    val repetitionCount: Int = 0,
    @Schema(description = "Дата следующего повторения", example = "2026-03-13T10:00:00")
    val nextRepetitionDate: java.time.LocalDateTime?
)
