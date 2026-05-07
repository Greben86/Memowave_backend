package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Слово - перевод: запрос")
data class WordRequest(
    @Schema(description = "Идентификатор категории", example = "123")
    val categoryId: Long?,
    @Schema(description = "Слово", example = "example")
    @Size(min = 1, max = 255, message = "Слово должно содержать от 1 до 255 символов")
    @NotBlank(message = "Слово не может быть пустыми")
    val text: String?,
    @Schema(description = "Перевод", example = "пример")
    @Size(min = 1, max = 255, message = "Перевод должен содержать от 1 до 255 символов")
    @NotBlank(message = "Перевод не может быть пустыми")
    val translate: String?,
    @Schema(description = "Пример", example = "It is example")
    @Size(max = 255, message = "Пример должен содержать не более 255 символов")
    val example: String?,
    @Schema(description = "Ссылка на изображение", example = "http://...")
    @Size(max = 255, message = "Ссылка должна содержать не более 255 символов")
    val imageUrl: String?,
    @Schema(description = "Количество повторений", example = "0")
    val repetitionCount: Int = 0,
    @Schema(description = "Дата следующего повторения", example = "2026-03-13T10:00:00")
    val nextRepetitionDate: java.time.LocalDateTime?,
    @Schema(description = "Качество повторения", example = "0")
    var quality: Long,
    @Schema(description = "Предыдущий коэффициент повторения", example = "2.5")
    var prevEaseFactor: Double,
    @Schema(description = "Предыдущий интервал повторения", example = "0")
    var prevInterval: Long
)
