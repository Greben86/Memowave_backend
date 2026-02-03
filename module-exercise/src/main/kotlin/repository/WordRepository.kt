package dev.greben.memowave.repository

import dev.greben.memowave.entities.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : JpaRepository<Word, Long> {
}