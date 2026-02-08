package dev.greben.memowave.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * Сервис импортирования слов
 */
@Service
@Transactional
class ImportService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}")
    private val backet: String
) {

    fun import(fileName: String) {
        val args = GetObjectArgs.builder()
            .bucket(backet)
            .`object`(fileName)
            .build()
        minioClient.getObject(args).use { stream ->
            println(String(stream.readAllBytes()))
        }
    }
}