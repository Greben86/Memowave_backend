package dev.greben.memowave.repository

import dev.greben.memowave.entities.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {

    fun findByName(name: String): Category?
}