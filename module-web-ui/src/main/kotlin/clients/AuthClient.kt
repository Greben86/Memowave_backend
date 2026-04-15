package dev.greben.memowave.clients

import dev.greben.memowave.dto.JwtAuthenticationResponse
import dev.greben.memowave.dto.SignInRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "AUTH-CLIENT", url = "http://user-management-service:8080")
interface AuthClient {

    @PostMapping(value = ["/api/auth/login"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signIn(request: SignInRequest): JwtAuthenticationResponse
}