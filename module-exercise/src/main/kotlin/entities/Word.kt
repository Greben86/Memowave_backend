package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "`words`")
class Word(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "words_generator")
    @SequenceGenerator(name = "words_generator", sequenceName = "words_seq", allocationSize = 1)
    var id: Long = 0L,
    @Column(name = "category_id", nullable = false)
    private var categoryId: Long?,
    @Column(name = "text", nullable = false)
    private var text: String?,
    @Column(name = "translate", nullable = false)
    private var translate: String?,
    @Column(name = "example", nullable = true)
    private var example: String?,
    @Column(name = "image_url", nullable = true)
    private var imageUrl: String?
) : AbstractEntity() {
    constructor(): this(categoryId = null, text = null, translate = null, example = null, imageUrl = null)
}