package dev.greben.memowave.controller

import dev.greben.memowave.clients.CategoryClient
import dev.greben.memowave.service.ImportService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin")
class FileUploadController(
    private val client: CategoryClient,
    private val service: ImportService
) {

    @GetMapping("/upload")
    fun uploadPage(model: Model): String {
        val categories = client.getAllCategories()
        model.addAttribute("categories", categories) // Передаем список в HTML
        return "upload"
    }

    @PostMapping("/upload")
    fun handleFileUpload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("category") category: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("message", "Файл не выбран!")
            return "redirect:/admin/upload"
        }

        val fileName = file.originalFilename
        service.uploadIntoCategory(file.inputStream, fileName!!, category)
        println("Загружен файл: $fileName, размер: ${file.size} байт")

        redirectAttributes.addFlashAttribute("message", "Файл '$fileName' успешно загружен!")
        return "redirect:/admin/upload"
    }
}