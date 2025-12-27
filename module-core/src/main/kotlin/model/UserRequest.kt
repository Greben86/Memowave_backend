package dev.greben.memowave.model

data class UserRequest(
    var username: String,
    var password: String,
    var imageUrl: String,
    var email: String
)
