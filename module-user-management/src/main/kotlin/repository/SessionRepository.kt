package dev.greben.memowave.repository

import dev.greben.memowave.entities.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface SessionRepository : JpaRepository<Session, Long> {

    @Query("select s from Session s where s.id = :id and s.isDenied = false and s.updatedAt > :start order by s.updatedAt desc limit 1")
    fun findActiveSessionById(id: Long, start: LocalDateTime): Optional<Session>

    @Query("select s from Session s where s.name = :name and s.isDenied = false and s.userId = :userId and s.updatedAt > :start order by s.updatedAt desc limit 1")
    fun findActiveSessionByName(name: String, userId: Long, start: LocalDateTime): Optional<Session>

    @Query("select s from Session s where s.isDenied = false and s.userId = :userId and s.updatedAt > :start order by s.updatedAt desc")
    fun findActiveSessionsByUserId(userId: Long, start: LocalDateTime): List<Session>
}