package dev.greben.memowave.service

import dev.greben.memowave.entities.User
import dev.greben.memowave.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    var repository: UserRepository
) {


    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    fun getByUsername(username: String?): User {
        return repository.findByUsername(username!!)
    }

    /**
     * Получение пользователя по имени пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username: String? -> this.getByUsername(username) }
    }
}