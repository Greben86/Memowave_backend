package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "`categories`")
class Category(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_generator")
    @SequenceGenerator(name = "categories_generator", sequenceName = "categories_seq", allocationSize = 1)
    var id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = false)
    var pack: Pack?,
    @Column(name = "name", nullable = false)
    var name: String?,
    @Column(name = "description", nullable = false)
    var description: String?,
    @Column(name = "color", nullable = false)
    var color: String?
) : AbstractEntity() {
    constructor(): this(pack = null, name = null, description = null, color = null)
}