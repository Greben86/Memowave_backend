package dev.greben.memowave.client

import dev.greben.memowave.utils.Constants
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "USER-MANAGEMENT-SERVICE")
interface SessionClient {

    @GetMapping(value = ["/api/sessions/{sessionId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSessionById(
        @PathVariable("sessionId") sessionId: Long,
        @RequestHeader(Constants.AUTH_HEADER_NAME) token: String): ResponseEntity<Void>
}