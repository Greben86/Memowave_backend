package dev.greben.memowave.service

import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.dto.UserResponse
import dev.greben.memowave.entities.User
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
    var repository: UserRepository
) {

    fun getById(id: Long): UserResponse? {
        return repository.findById(id)
            .map { UserResponse(
                id = it.id,
                username = it.username,
                imageUrl = it.getImageUrl(),
                email = it.getEmail())
            }
            .orElseGet { null }
    }

    /**
     * Выборка всех пользователей
     *
     * @return список пользователей
     */
    fun getAllUsers(): List<UserResponse> {
        return repository.findByUserRoleNot("ROLE_ADMIN").stream()
            .map { UserResponse(id = it.id, username = it.username, imageUrl = it.getImageUrl(), email = it.getEmail()) }
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
        user.setUserRole("ROLE_ADMIN")
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

        val entity = User(
            username = user.username,
            userRole = "ROLE_USER",
            passwordHash = encodedPassword,
            imageUrl = user.imageUrl,
            email = user.email)
        return repository.save(entity)
    }

    /**
     * Обновление пользователя
     *
     * @return пользователь
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveUser(request: UserResponse?): UserResponse? {
        val user: User? = getByUsername(request!!.username!!)
        user?.setUsername(request.username)
        user?.setImageUrl(request.imageUrl)
        user?.setEmail(request.email)
        if (user != null) {
            repository.save(user)
            return UserResponse(
                id = user.id,
                username = user.username,
                imageUrl = user.getImageUrl(),
                email = user.getEmail())
        }

        return null
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
     * Получение пользователя по имени пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    fun userDetailsService(): UserDetailsService =
        UserDetailsService(this::getByUsername)

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
}