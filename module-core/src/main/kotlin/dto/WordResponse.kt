package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Слово - перевод: ответ")
data class WordResponse(
    @Schema(description = "Идентификатор слова", example = "123")
    val id: Long?,
    @Schema(description = "Идентификатор категории", example = "123")
    val categoryId: Long?,
    @Schema(description = "Слово", example = "example")
    var text: String,
    @Schema(description = "Перевод", example = "пример")
    var translate: String,
    @Schema(description = "Пример", example = "It is example")
    var example: String,
    @Schema(description = "Ссылка на изображение", example = "http://...")
    var imageUrl: String?,
    @Schema(description = "Идентификатор пользователя", example = "0")
    var userId: Long = 0L,
    @Schema(description = "Количество повторений", example = "0")
    var repetitionCount: Int = 0,
    @Schema(description = "Дата следующего повторения", example = "2026-03-13T10:00:00")
    var nextRepetitionDate: LocalDateTime?,
    @Schema(description = "Качество повторения", example = "0")
    var quality: Long,
    @Schema(description = "Предыдущий коэффициент повторения", example = "2.5")
    var prevEaseFactor: Double,
    @Schema(description = "Предыдущий интервал повторения", example = "0")
    var prevInterval: Long,
    @Schema(description = "Дата добавления слова", example = "2026-03-13T10:00:00")
    var createdAt: LocalDateTime?,
    @Schema(description = "Дата обновления слова", example = "2026-03-13T10:00:00")
    var updatedAt: LocalDateTime?,
    @Schema(description = "Параметр стабильности", example = "2.5")
    var stability: Double,
    @Schema(description = "Параметр сложности", example = "2.5")
    var difficulty: Double,
    @Schema(description = "Текущий интервал повторения", example = "0")
    var interval: Long,
    @Schema(description = "Дата следующего повторения", example = "2026-03-13T10:00:00")
    var dueDate: LocalDateTime?,
    @Schema(description = "Количество повторений", example = "0")
    var reviewCount: Long,
    @Schema(description = "Дата последнего повторения", example = "2026-03-13T10:00:00")
    var lastReview: LocalDateTime?,
    @Schema(description = "Параметр phase", example = "0")
    var phase: Long
)
