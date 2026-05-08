package dev.greben.memowave.service

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.entities.Category
import dev.greben.memowave.entities.Word
import dev.greben.memowave.mapper.CategoryMapper
import dev.greben.memowave.repository.CategoryRepository
import dev.greben.memowave.repository.WordRepository
import dev.greben.memowave.util.getCurrentUserId
import dev.greben.memowave.util.isCurrentUserAdmin
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

/**
 * Сервис управления категориями
 */
@Service
@Transactional
class CategoryService(
    private val packService: PackService,
    private val repository: CategoryRepository,
    private val mapper: CategoryMapper,
    private val wordRepository: WordRepository
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Выборка всех категорий слов
     *
     * @return список категорий
     */
    fun getById(id: Long): Category? =
        repository.findById(id)
            .getOrNull()

    /**
     * Выборка всех категорий слов
     *
     * @return список категорий
     */
    fun getAllCategories(): List<CategoryResponse> {
        val currentUserId = getCurrentUserId()
        val isAdmin = isCurrentUserAdmin()

        val allCategories = when(isAdmin){
            true -> repository.findAll()
            false -> repository.findAllByUserId(currentUserId)
        }

        return allCategories.stream()
            .map { mapper.toDto(it) }
            .toList()
    }

    /**
     * Выборка категори по Id
     *
     * @return список категорий
     */
    fun getCategoryById(categoryId: Long): CategoryResponse? =
        repository.findById(categoryId)
            .map { mapper.toDto(it) }
            .getOrNull()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveCategory(request: CategoryRequest?): CategoryResponse? {
        if (request == null) {
            log.warn { "!! Request can not be null" }
            return null
        }

        val pack = packService.lookingForName("ENG")
        if (pack == null) {
            log.warn { "!! Pack with name ENG not found" }
            return null
        }

        val currentUserId = getCurrentUserId()
        var entity = mapper.fromDto(request)
        entity.pack = pack
        entity.userId = currentUserId
        entity = repository.save(entity)

        return mapper.toDto(entity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateCategory(categoryId: Long, request: CategoryRequest?): CategoryResponse? {
        if (request == null) {
            log.warn { "!! Request can not be null" }
            return null
        }

        val entity = repository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("!! Category with id=$categoryId not found") }

        // Проверка прав доступа
//        checkAccessRights(entity)

        // Обновляем категорию
        val updatedEntity = mapper.updateFromDto(entity, request)
        val savedEntity = repository.save(updatedEntity)

        return mapper.toDto(savedEntity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun deleteCategory(categoryId: Long) {
        val entity = repository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("!! Category with id=$categoryId not found") }

        // Проверка прав доступа
        checkAccessRights(entity)

        // Удаляем все слова категории
        wordRepository.deleteByCategoryId(categoryId)
        
        // Удаляем категорию
        repository.delete(entity)
        
        log.info { "Category $categoryId deleted by user ${getCurrentUserId()}" }
    }

    /**
     * Проверка прав доступа к категории
     */
    private fun checkAccessRights(category: Category) {
        val currentUserId = getCurrentUserId()
        val isAdmin = isCurrentUserAdmin()
        
        // Администратор может редактировать любые категории
        if (isAdmin) {
            return
        }
        
        // Общие категории (userId = 0) могут редактировать только администраторы
        if (category.userId == 0L) {
            throw SecurityException("Only administrators can edit or delete shared categories")
        }
        
        // Пользователь может редактировать только свои категории
        if (category.userId != currentUserId) {
            throw SecurityException("User can only edit or delete their own categories")
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun copyCategoryForUser(categoryId: Long): CategoryResponse? {
        val currentUserId = getCurrentUserId()
        // Проверяем, что исходная категория существует
        val sourceCategory = repository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("!! Category with id=$categoryId not found") }

//        if (sourceCategory.userId != 0L) {
//            throw IllegalArgumentException("!! Only categories with userId=0 can be copied")
//        }

        // Создаем новую категорию на основе исходной, но с новым userId
        val newCategory = Category(
            pack = sourceCategory.pack,
            name = sourceCategory.name,
            description = sourceCategory.description,
            color = sourceCategory.color,
            iconName = sourceCategory.iconName,
            userId = currentUserId
        )
        
        // Сохраняем новую категорию
        val savedCategory = repository.save(newCategory)

        // Копируем все слова из исходной категории в новую
        val words = wordRepository.findByCategoryId(categoryId)
        val savedWords = words.map { word ->
            val newWord = Word(
                category = savedCategory,
                text = word.text,
                translate = word.translate,
                example = word.example,
                imageUrl = word.imageUrl,
                userId = currentUserId,
                repetitionCount = 0,
                nextRepetitionDate = LocalDateTime.now(),
                quality = 0,
                prevEaseFactor = 2.5,
                prevInterval = 0,
                stability = 2.5,
                difficulty = 2.5,
                interval = 0,
                dueDate = LocalDateTime.now(),
                reviewCount = 0,
                lastReview = null,
                phase = 0
            )
            wordRepository.save(newWord)
        }

        // Логируем результат
        log.info { "Category $categoryId copied for user $currentUserId. New category: ${savedCategory.id}, words: ${savedWords.size}" }
        
        // Возвращаем DTO новой категории
        return mapper.toDto(savedCategory)
    }
}