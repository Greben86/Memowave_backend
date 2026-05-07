package dev.greben.memowave.configuration

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfiguration(
    @Value("\${minio.endpoint}")
    val minioEndpoint: String,
    @Value("\${minio.accessKey}")
    val accessKey: String,
    @Value("\${minio.secretKey}")
    val secretKey: String
) {

    @Bean
    fun minioClient(): MinioClient =
        MinioClient.builder()
            .credentials(accessKey, secretKey)
            .endpoint(minioEndpoint, 9000, false)
            .build()
}