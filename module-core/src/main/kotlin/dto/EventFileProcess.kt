package dev.greben.memowave.dto

import java.io.Serializable

data class EventFileProcess(
    val key: String,
    val backet: String,
    val fileName: String,
    val status: FileProcessStatus
) : Serializable
