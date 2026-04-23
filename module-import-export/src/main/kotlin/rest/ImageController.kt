package dev.greben.memowave.rest

import dev.greben.memowave.dto.ImageUploadResponse
import dev.greben.memowave.service.ImageService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
@Tag(name = "REST API: Изображения")
@SecurityRequirement(name = "jwt-token")
class ImageController(
    private val imageService: ImageService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Загрузить изображение в хранилище")
    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun handleImageUpload(@RequestParam("file") file: MultipartFile): ImageUploadResponse {
        val fileName = file.originalFilename
        log.info { "Загрузка изображения: $fileName, размер: ${file.size} байт" }
        return imageService.uploadImage(file.inputStream, fileName!!)
    }

    @Operation(summary = "Получить изображение из хранилища")
    @GetMapping("{fileName:.+}")
    fun handleImageDownload(@PathVariable("fileName") fileName: String): ResponseEntity<InputStreamResource> {
        log.info { "Получение изображения: $fileName" }
        
        val resource = imageService.downloadImage(fileName)

        val contentDisposition = ContentDisposition.builder("inline")
            .filename(fileName)
            .build()
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource)
    }

    @Operation(summary = "Удалить изображение из хранилища")
    @DeleteMapping("{fileName:.+}")
    @ResponseStatus(HttpStatus.OK)
    fun handleImageDelete(@PathVariable("fileName") fileName: String) {
        log.info { "Удаление изображения: $fileName" }

        imageService.deleteImage(fileName)
    }
}