package dev.greben.memowave.service

import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.dto.UserResponse
import dev.greben.memowave.entities.User
import dev.greben.memowave.mapper.UserMapper
import dev.greben.memowave.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис управления пользователями
 */
@Service
@Transactional
class UserService(
    private val repository: UserRepository,
    private val mapper: UserMapper
) {

    /**
     * Получить пользователя по идентификатору
     *
     * @return информация о пользователе
     */
    fun getById(id: Long): UserResponse? {
        return repository.findById(id)
            .map { mapper.toDto(it) }
            .orElseGet { null }
    }

    /**
     * Выборка всех пользователей
     *
     * @return список пользователей
     */
    fun getAllUsers(): List<UserResponse> {
        return repository.findByUserRoleNot("ROLE_ADMIN").stream()
            .map { mapper.toDto(it) }
            .toList()
    }

    /**
     * Выдача прав администратора пользователю
     *
     * @param id идентификатор пользователя
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun setAdmin(id: Long) {
        val user = repository.findById(id)
            .orElseThrow { IllegalArgumentException("Пользователь не найден") }
        user.userRole = "ROLE_ADMIN"
        repository.save(user)
    }

    /**
     * Регистрация нового пользователя
     *
     * @param user запрос на регистрацию пользователя
     * @param encodedPassword хеш пароля
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun register(user: SignUpRequest, encodedPassword: String): User {
        check(getByUsername(user.username) == null) {
            "Такой пользователь с логином '${user.username}' уже есть"
        }

        val entity = mapper.fromDto(user, encodedPassword)
        return repository.save(entity)
    }

    /**
     * Обновление пользователя
     *
     * @return пользователь
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateCurrentUser(request: UserResponse?): UserResponse? {
        val user = getCurrentUser()!!
        val foundUser: User? = getByUsername(request!!.username!!)
        check(foundUser != null && user.id != foundUser.id) {
            "Такой пользователь с логином '${request.username}' уже есть"
        }

        val updated = mapper.updateFromDto(user, request)
        repository.save(updated)
        return mapper.toDto(updated)
    }

    /**
     * Удаление пользователя
     *
     * @param id идентификатор пользователя
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun deleteUser(id: Long) {
        val user: User? = getCurrentUser()
        check(user?.id != id) { "Нельзя удалить себя" }
        repository.deleteById(id)
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    fun getByUsername(username: String): User? =
        repository.findByUsername(username)

    /**
     * Получение пользователя по email пользователя
     *
     * @return пользователь
     */
    fun getByEmail(email: String): User? =
        repository.findByEmail(email)

    /**
     * Получение пользователя по имени пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    fun userDetailsServiceByUserName(): UserDetailsService =
        UserDetailsService(this::getByUsername)

    /**
     * Получение пользователя по email пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    fun userDetailsServiceByEmail(): UserDetailsService =
        UserDetailsService(this::getByEmail)

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    fun getCurrentUser(): User? {
        // Получение имени пользователя из контекста Spring Security
        val username = SecurityContextHolder
            .getContext()
            .authentication
            .name

        return getByUsername(username)
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    fun currentUser(): UserResponse? {
        // Получение имени пользователя из контекста Spring Security
        val user = getCurrentUser() ?: return null

        return mapper.toDto(user)
    }

    /**
     * Смена пароля пользователя
     *
     * @param user пользователь
     * @param encodedPassword новый пароль
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun changePassword(user: User, encodedPassword: String) {
        // Обновление пароля
        user.passwordHash = encodedPassword
        repository.save(user)
    }
}