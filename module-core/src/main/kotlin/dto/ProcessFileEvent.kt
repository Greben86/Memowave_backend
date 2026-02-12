package dev.greben.memowave.dto

data class ProcessFileEvent(
    val key: String,
    val backet: String,
    val fileName: String,
    val status: String
)
