package dev.greben.memowave.repository

import dev.greben.memowave.entities.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {

    @Query("select c from Category c where c.userId = :userId")
    fun findAllByUserId(userId: Long): List<Category>
}