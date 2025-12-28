package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@MappedSuperclass
abstract class AbstractEntity {

    @Column(name = "created_at")
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at")
    @CreationTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
}