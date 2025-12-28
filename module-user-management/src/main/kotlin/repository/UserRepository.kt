package dev.greben.memowave.repository

import dev.greben.memowave.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun findByUserRoleNot(userRole: String): List<User>
}