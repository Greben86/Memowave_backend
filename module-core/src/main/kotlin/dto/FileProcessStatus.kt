package dev.greben.memowave.dto

enum class FileProcessStatus(
    name: String
) {
    SUCCESS("Success"), FAIL("Fail"), ERROR("Error")
}