package dev.greben.memowave.service

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.entities.Category
import dev.greben.memowave.entities.Word
import dev.greben.memowave.mapper.CategoryMapper
import dev.greben.memowave.repository.CategoryRepository
import dev.greben.memowave.repository.WordRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
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
    fun getAllCategories(): List<CategoryResponse> =
        repository.findAll().stream()
            .map { mapper.toDto(it) }
            .toList()

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

        var entity = mapper.fromDto(request)
        entity.pack = pack
        entity.userId = request.userId
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

    fun lookingForName(name: String): Category? = repository.findByName(name)

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
    
    /**
     * Получение идентификатора текущего пользователя из JWT токена
     */
    private fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        authentication.details
        
        // Предполагаем, что в principal находится идентификатор пользователя
        return when (principal) {
            is Long -> principal
            is String -> principal.toLongOrNull() ?: throw IllegalStateException("Invalid user ID format in token $principal")
            else -> throw IllegalStateException("User ID not found in token")
        }
    }
    
    /**
     * Проверка, является ли текущий пользователь администратором
     */
    private fun isCurrentUserAdmin(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun copyCategoryForUser(categoryId: Long, userId: Long): CategoryResponse? {
        // Проверяем, что исходная категория существует и принадлежит общему пользователю (userId = 0)
        val sourceCategory = repository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("!! Category with id=$categoryId not found") }
        
        if (sourceCategory.userId != 0L) {
            throw IllegalArgumentException("!! Only categories with userId=0 can be copied")
        }

        // Создаем новую категорию на основе исходной, но с новым userId
        val newCategory = Category(
            pack = sourceCategory.pack,
            name = sourceCategory.name,
            description = sourceCategory.description,
            color = sourceCategory.color,
            userId = userId
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
                repetitionCount = 0,
                nextRepetitionDate = LocalDateTime.now()
            )
            wordRepository.save(newWord)
        }

        // Логируем результат
        log.info { "Category $categoryId copied for user $userId. New category: ${savedCategory.id}, words: ${savedWords.size}" }
        
        // Возвращаем DTO новой категории
        return mapper.toDto(savedCategory)
    }
}