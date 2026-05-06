package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "`sessions`")
class Session(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessions_generator")
    @SequenceGenerator(name = "sessions_generator", sequenceName = "sessions_seq", allocationSize = 1)
    var id: Long = 0L,
    @Column(name = "name", nullable = false)
    var name: String?,
    @Column(name = "is_denied", nullable = false)
    var isDenied: Boolean?,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L
) : AbstractEntity() {
    constructor(): this(name = null, isDenied = false)
}