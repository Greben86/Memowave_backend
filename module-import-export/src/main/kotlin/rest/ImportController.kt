package dev.greben.memowave.rest

import dev.greben.memowave.service.ImportService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/import")
@Tag(name = "REST API: Импорт")
class ImportController(
    private val importService: ImportService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Выполнить загрузку файла")
    @PostMapping(value = ["/upload/{categoryId}/category"], consumes = [MediaType.ALL_VALUE])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun importIntoCategory(
        @PathVariable("categoryId") categoryId: Long,
        @RequestParam fileName: String,
        request: HttpServletRequest
    ) {
        log.info { "Загрузка файла $fileName в категорию id=$categoryId" }
        importService.uploadIntoCategory(request.inputStream, fileName, categoryId)
    }
}