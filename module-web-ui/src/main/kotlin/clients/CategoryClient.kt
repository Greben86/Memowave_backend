package dev.greben.memowave.clients

import dev.greben.memowave.configuration.FeignClientConfig
import dev.greben.memowave.dto.CategoryResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "EXERCISE-SERVICE", configuration = [FeignClientConfig::class])
interface CategoryClient {

    @GetMapping(value = ["/api/categories"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllCategories(): List<CategoryResponse>
}