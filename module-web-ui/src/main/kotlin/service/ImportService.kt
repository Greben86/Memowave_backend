package dev.greben.memowave.service

import dev.greben.memowave.configuration.RabbitQueueProperties
import dev.greben.memowave.dto.EventFileProcess
import dev.greben.memowave.dto.EventFileUpload
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.ObjectWriteArgs
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.*

/**
 * Сервис импортирования слов
 */
@Service
class ImportService(
    private val rabbitTemplate: RabbitTemplate,
    private val queueProperties: RabbitQueueProperties,
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}")
    private val backetName: String
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @RabbitListener(queues = ["#{@environment.getProperty('rabbitmq.queue.input')}"])
    fun receiveMessage(message: EventFileProcess?) {
        log.info { "Received message: $message" }
        minioClient.removeObject(
            RemoveObjectArgs.builder()
            .bucket(message!!.backet)
            .`object`(message.fileName)
            .build())
    }

    fun uploadIntoCategory(inputStream: InputStream, fileName: String, categoryId: Long) {
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
            println("Bucket '$backetName' created")
        }

        val key = createUniqueKey()
        val args = PutObjectArgs.builder()
            .bucket(backetName)
            .`object`(fileName)
            .stream(inputStream, -1, ObjectWriteArgs.MIN_MULTIPART_SIZE.toLong())
            .build()
        minioClient.putObject(args)

        rabbitTemplate.convertAndSend(queueProperties.output!!,
            EventFileUpload(key, backetName, fileName, categoryId)
        ) proc@{ message ->
            val tokenValue = SecurityContextHolder.getContext()
                ?.authentication
                ?.principal as String?
            message.messageProperties.headers["token"] = tokenValue
            return@proc message
        }
    }

    private fun createUniqueKey(): String =
        UUID.randomUUID().toString().replace("-", "")
}