package dev.greben.memowave.repository

import dev.greben.memowave.entities.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : JpaRepository<Word, Long> {

    @Query("select w from Word w where w.category.id=:categoryId")
    fun findByCategoryId(categoryId: Long): List<Word>
}