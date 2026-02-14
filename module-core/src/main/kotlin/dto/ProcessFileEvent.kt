package dev.greben.memowave.dto

import java.io.Serializable

data class ProcessFileEvent(
    val key: String,
    val backet: String,
    val fileName: String,
    val status: String
) : Serializable
