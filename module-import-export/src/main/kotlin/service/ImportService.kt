package dev.greben.memowave.service

import io.minio.MinioClient
import io.minio.ObjectWriteArgs
import io.minio.PutObjectArgs
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

/**
 * Сервис импортирования слов
 */
@Service
class ImportService(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.queue.name}")
    val queueName: String,
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}")
    private val backetName: String
) {

    fun upload(inputStream: InputStream, fileName: String) {
        val args = PutObjectArgs.builder()
            .bucket(backetName)
            .`object`(fileName)
            .stream(inputStream, -1, ObjectWriteArgs.MIN_MULTIPART_SIZE.toLong())
//            .stream(inputStream, inputStream.available().toLong(), -1)
            .build()
        minioClient.putObject(args)

        rabbitTemplate.convertAndSend(queueName, fileName)
    }
}