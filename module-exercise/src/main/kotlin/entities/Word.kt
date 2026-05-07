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
import java.time.LocalDateTime

@Entity
@Table(name = "`words`")
class Word(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "words_generator")
    @SequenceGenerator(name = "words_generator", sequenceName = "words_seq", allocationSize = 1)
    var id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category?,
    @Column(name = "text", nullable = false)
    var text: String?,
    @Column(name = "translate", nullable = false)
    var translate: String?,
    @Column(name = "example", nullable = true)
    var example: String?,
    @Column(name = "image_url", nullable = true)
    var imageUrl: String?,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L,
    @Column(name = "repetition_count", nullable = false)
    var repetitionCount: Int = 0,
    @Column(name = "next_repetition_date", nullable = true)
    var nextRepetitionDate: LocalDateTime?,
    @Column(name = "quality", nullable = false)
    var quality: Long,
    @Column(name = "prev_ease_factor", nullable = false)
    var prevEaseFactor: Double,
    @Column(name = "prev_interval", nullable = false)
    var prevInterval: Long
) : AbstractEntity() {
    constructor(): this(category = null, text = null, translate = null, example = null, imageUrl = null,
        repetitionCount = 0, nextRepetitionDate = null, quality = 0, prevEaseFactor = 2.5, prevInterval = 0)
}