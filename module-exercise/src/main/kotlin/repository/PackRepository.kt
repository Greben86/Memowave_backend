package dev.greben.memowave.repository

import dev.greben.memowave.entities.Pack
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PackRepository : JpaRepository<Pack, Long> {

    fun findByName(name: String): Pack?
}