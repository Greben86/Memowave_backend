package dev.greben.memowave.dto

data class UploadFileEvent(
    val key: String,
    val backet: String,
    val fileName: String
)
