package dev.greben.memowave.dto

import java.io.Serializable

data class UploadFileEvent(
    val key: String,
    val backet: String,
    val fileName: String,
    val categoryId: Long
) : Serializable
