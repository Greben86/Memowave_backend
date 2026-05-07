package dev.greben.memowave.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ загрузки изображения")
data class ImageUploadResponse(
    @Schema(description = "Название файла изображения", example = "qwerty123.jpg")
    var fileName: String
)