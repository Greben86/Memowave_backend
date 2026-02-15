package dev.greben.memowave.service

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import dev.greben.memowave.configuration.RabbitQueueProperties
import dev.greben.memowave.dto.EventFileUpload
import dev.greben.memowave.dto.FileProcessStatus
import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.mapper.FileEventMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.GetObjectArgs
import io.minio.MinioClient
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception


/**
 * Сервис импортирования слов
 */
@Service
@Transactional
class ImportService(
    private val serviceWord: WordService,
    private val rabbitTemplate: RabbitTemplate,
    private val queueProperties: RabbitQueueProperties,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val minioClient: MinioClient,
    private val eventMapper: FileEventMapper
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @RabbitListener(queues = ["#{@environment.getProperty('rabbitmq.queue.input')}"])
    fun receiveMessage(message: EventFileUpload?) {
        log.info { "Received message: $message" }
        applicationEventPublisher.publishEvent(message!!)
    }

    @Async
    @EventListener
    fun import(event: EventFileUpload) {
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
                        imageUrl = null)
                    words.add(word)
                    log.info { "New word: $word" }
                }
            }

            val saved = serviceWord.saveWords(words, event.categoryId)
            log.info { "Saved ${saved.size} words"}

            rabbitTemplate.convertAndSend(queueProperties.output!!,
                eventMapper.toProcess(event,
                    if (words.size == saved.size) FileProcessStatus.SUCCESS else FileProcessStatus.FAIL))
        } catch (ex: Exception) {
            log.error(ex) { "!! Error ${ex.message}" }
            rabbitTemplate.convertAndSend(queueProperties.output!!,
                eventMapper.toProcess(event, FileProcessStatus.ERROR))
        }
    }
}
