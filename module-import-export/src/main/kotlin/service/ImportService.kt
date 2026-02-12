package dev.greben.memowave.service

import dev.greben.memowave.dto.ProcessFileEvent
import dev.greben.memowave.dto.UploadFileEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.MinioClient
import io.minio.ObjectWriteArgs
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.UUID

/**
 * Сервис импортирования слов
 */
@Service
class ImportService(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.queue.output}")
    private val queueName: String,
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}")
    private val backetName: String
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @RabbitListener(queues = ["#{@environment.getProperty('rabbitmq.queue.input')}"])
    fun receiveMessage(message: ProcessFileEvent?) {
        log.info { "Received message: $message" }
        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(message!!.backet)
            .`object`(message.fileName)
            .build())
    }

    fun upload(inputStream: InputStream, fileName: String) {
        val key = createUniqueKey()
        val args = PutObjectArgs.builder()
            .bucket(backetName)
            .`object`(fileName)
            .stream(inputStream, -1, ObjectWriteArgs.MIN_MULTIPART_SIZE.toLong())
            .build()
        minioClient.putObject(args)

        rabbitTemplate.convertAndSend(queueName, UploadFileEvent(key, backetName, fileName))
    }

    private fun createUniqueKey(): String =
        UUID.randomUUID().toString().replace("-", "")
}