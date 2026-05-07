package dev.greben.memowave.service

import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.dto.UserResponse
import dev.greben.memowave.entities.User
import dev.greben.memowave.mapper.UserMapper
import dev.greben.memowave.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Random
import java.util.function.Predicate

/**
 * Сервис управления пользователями
 */
@Service
@Transactional
class UserService(
    private val repository: UserRepository,
    private val mapper: UserMapper,
    private val emailNotificationService: EmailNotificationService
) {

    val ALPHABET: String = "1234567890"
    val lengthOTP: Int = 5;

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
     * Верификация электронной почты пользователя
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
     * Верификация почты пользователя по OTP
     *
     * @param id идентификатор пользователя
     * @param code OTP для верификации
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun verifyEmail(id: Long, code: String) {
        val user = repository.findById(id)
            .orElseThrow { IllegalArgumentException("Пользователь не найден") }

        if (code == user.otpCode) {
            user.emailVerified = true
            repository.save(user)
        } else {
            throw IllegalArgumentException("Код не подходит")
        }
    }

    /**
     * Проверка OTP-кода текущего пользователя
     *
     * @param code OTP для верификации
     */
    fun getOTPCodeCurrentUser(code: String): Boolean {
        val user = getCurrentUser() ?: throw IllegalArgumentException("Пользователь не найден")
        if (code == user.otpCode) {
            return true
        } else {
            throw IllegalArgumentException("Код не подходит")
        }
    }

    /**
     * Отправка нового OTP пользователю
     *
     * @param id идентификатор пользователя
     */
    fun sendOtpCode(id: Long) {
        val user = repository.findById(id)
            .orElseThrow { IllegalArgumentException("Пользователь не найден") }

        val code = generateKey() { it == user.otpCode }
        user.otpCode = code
        repository.save(user)
        emailNotificationService.sendOtpCode(user.email!!, user.otpCode!!)
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
        entity.otpCode = generateKey() { false }
        emailNotificationService.sendOtpCode(entity.email!!, entity.otpCode!!)
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

    /**
     * Метод формирования уникального кода ссылки для короткой ссылки
     * Каждый символ кода выбирается случайным образом, после чего код проверяется на совпадение с уже
     * сохраненными активными кодами, если совпадений нет, то код принимается, если нет - пробуем еще раз
     *
     * @param size размер кода
     * @param existChecker лямбда для проверки уникальности кода
     *
     * @return уникальный для пользователя код
     */
    private fun generateKey(existChecker: Predicate<String?>): String {
        var random = Random()
        var key: String
        do {
            // Генерация строки случайным образом
            val sb = StringBuilder()
            for (i in 0..<lengthOTP) {
                sb.append(ALPHABET.get(random.nextInt(ALPHABET.length)))
            }
            key = sb.toString()
        } while (existChecker.test(key))

        return key
    }
}