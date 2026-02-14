package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "`packs`")
class Pack(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "packs_generator")
    @SequenceGenerator(name = "packs_generator", sequenceName = "packs_seq", allocationSize = 1)
    var id: Long = 0L,
    @Column(name = "name", nullable = false)
    var name: String?,
    @Column(name = "description", nullable = false)
    var description: String?,
    @Column(name = "language", nullable = false)
    var language: String?
) : AbstractEntity() {
    constructor(): this(name = null, description = null, language = null)
}