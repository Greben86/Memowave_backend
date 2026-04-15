package dev.greben.memowave.client

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.utils.Constants.AUTH_HEADER_NAME
import jakarta.validation.Valid
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "WORD-CLIENT", url = "http://exercise-service:8080")
interface WordClient {

    @PostMapping(value = ["/api/words/{categoryId}/add/all"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addWords(
        @PathVariable("categoryId") categoryId: Long,
        @RequestBody @Valid request: List<WordRequest>,
        @RequestHeader(AUTH_HEADER_NAME) token: String): List<WordResponse>
}