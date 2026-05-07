package dev.greben.memowave.service

import dev.greben.memowave.dto.ImageUploadResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.ObjectWriteArgs
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.UUID

/**
 * Сервис изображений
 */
@Service
class ImageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}")
    private val backetName: String
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    fun uploadImage(inputStream: InputStream, originFileName: String): ImageUploadResponse {
        // Make bucket if not exist.
        val found =
            minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(backetName)
                .build())
        if (!found) {
            // Make a new bucket
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(backetName)
                .build())
            log.info { "Bucket '$backetName' created" }
        }

        val key = createUniqueKey()
        
        // Extract file extension from fileName
        val fileExtension = originFileName.substringAfterLast(".", "")
        val fileName = if (fileExtension.isEmpty()) key else "$key.$fileExtension"
        
        val args = PutObjectArgs.builder()
            .bucket(backetName)
            .`object`(fileName)
            .stream(inputStream, -1, ObjectWriteArgs.MIN_MULTIPART_SIZE.toLong())
            .build()
        minioClient.putObject(args)

        return ImageUploadResponse(fileName)
    }

    fun downloadImage(fileName: String): InputStreamResource {
        val args = GetObjectArgs.builder()
            .bucket(backetName)
            .`object`(fileName)
            .build()

        return InputStreamResource(minioClient.getObject(args))
    }

    private fun createUniqueKey(): String =
        UUID.randomUUID().toString().replace("-", "")

    fun deleteImage(fileName: String) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(backetName)
                .`object`(fileName)
                .build())
    }
}