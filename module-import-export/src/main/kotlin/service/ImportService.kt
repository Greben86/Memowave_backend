package dev.greben.memowave.service

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import dev.greben.memowave.client.WordClient
import dev.greben.memowave.configuration.RabbitQueueProperties
import dev.greben.memowave.dto.EventFileUpload
import dev.greben.memowave.dto.FileProcessStatus
import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.mapper.FileEventMapper
import dev.greben.memowave.utils.Constants.AUTH_BEARER_PREFIX
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.GetObjectArgs
import io.minio.MinioClient
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service
import java.lang.Exception
import java.time.LocalDateTime

/**
 * Сервис импортирования слов
 */
@Service
class ImportService(
    private val rabbitTemplate: RabbitTemplate,
    private val queueProperties: RabbitQueueProperties,
    private val minioClient: MinioClient,
    private val wordClient: WordClient,
    private val eventMapper: FileEventMapper
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @RabbitListener(queues = ["#{@environment.getProperty('rabbitmq.queue.input')}"])
    fun receiveMessage(
        message: EventFileUpload?,
        @Header("token") token: String
    ) {
        log.info { "Received message: $message" }
        import(message!!, token);
    }

    fun import(event: EventFileUpload, token: String) {
        try {
            val args = GetObjectArgs.builder()
                .bucket(event.backet)
                .`object`(event.fileName)
                .build()

            val parser = CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build()

            val words = ArrayList<WordRequest>()
            minioClient.getObject(args).use { stream ->
                val csvReader = CSVReaderBuilder(stream.reader())
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build()
                csvReader.forEach {
                    val word = WordRequest(
                        categoryId = event.categoryId,
                        text = it[0],
                        translate = it[1],
                        example = it[2],
                        imageUrl = null,
                        repetitionCount = 0,
                        nextRepetitionDate = LocalDateTime.now())
                    words.add(word)
                    log.info { "New word: $word" }
                }
            }

            val saved = wordClient.addWords(words, AUTH_BEARER_PREFIX + token)
            log.info { "Saved ${saved.size} words"}

            rabbitTemplate.convertAndSend(queueProperties.output!!,
                eventMapper.toProcess(event,
                    if (saved.isNotEmpty()) FileProcessStatus.SUCCESS else FileProcessStatus.FAIL))
        } catch (ex: Exception) {
            log.error(ex) { "!! Error ${ex.message}" }
            rabbitTemplate.convertAndSend(queueProperties.output!!,
                eventMapper.toProcess(event, FileProcessStatus.ERROR))
        }
    }
}