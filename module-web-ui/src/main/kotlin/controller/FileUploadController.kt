package dev.greben.memowave.controller

import dev.greben.memowave.service.ImportService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin")
class FileUploadController(
    private val service: ImportService
) {

    @GetMapping("/upload")
    fun uploadPage(): String = "upload"

    @PostMapping("/upload")
    fun handleFileUpload(
        @RequestParam("file") file: MultipartFile,
        redirectAttributes: RedirectAttributes
    ): String {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("message", "Файл не выбран!")
            return "redirect:/admin/upload"
        }

        val fileName = file.originalFilename
        service.uploadIntoCategory(file.inputStream, fileName!!, 1)
        println("Загружен файл: $fileName, размер: ${file.size} байт")

        redirectAttributes.addFlashAttribute("message", "Файл '$fileName' успешно загружен!")
        return "redirect:/admin/upload"
    }
}