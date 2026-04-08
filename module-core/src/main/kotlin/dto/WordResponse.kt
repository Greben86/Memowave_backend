package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Слово - перевод: ответ")
data class WordResponse(
    @Schema(description = "Идентификатор слова", example = "123")
    val id: Long?,
    @Schema(description = "Категория", example = "Не правильные глаголы")
    var category: String,
    @Schema(description = "Слово", example = "example")
    var text: String,
    @Schema(description = "Перевод", example = "пример")
    var translate: String,
    @Schema(description = "Пример", example = "It is example")
    var example: String,
    @Schema(description = "Ссылка на изображение", example = "http://...")
    var imageUrl: String?,
    @Schema(description = "Количество повторений", example = "0")
    var repetitionCount: Int = 0,
    @Schema(description = "Дата следующего повторения", example = "2026-03-13T10:00:00")
    var nextRepetitionDate: LocalDateTime?,
    @Schema(description = "Дата добавления слова", example = "2026-03-13T10:00:00")
    var createdAt: LocalDateTime?,
    @Schema(description = "Дата обновления слова", example = "2026-03-13T10:00:00")
    var updatedAt: LocalDateTime?
)
