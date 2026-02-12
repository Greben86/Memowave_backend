package dev.greben.memowave.service

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import dev.greben.memowave.dto.ProcessFileEvent
import dev.greben.memowave.dto.UploadFileEvent
import dev.greben.memowave.entities.Word
import dev.greben.memowave.mapper.FileEventMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * Сервис импортирования слов
 */
@Service
@Transactional
class ImportService(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.queue.output}")
    private val queueName: String,
    private val serviceWord: WordService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val minioClient: MinioClient,
    private val eventMapper: FileEventMapper
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @RabbitListener(queues = ["#{@environment.getProperty('rabbitmq.queue.input')}"])
    fun receiveMessage(message: UploadFileEvent?) {
        log.info { "Received message: $message" }
        val event = eventMapper.toProcess(message!!)
        applicationEventPublisher.publishEvent(event)
    }

    @Async
    @EventListener
    fun import(event: ProcessFileEvent) {
        val args = GetObjectArgs.builder()
            .bucket(event.backet)
            .`object`(event.fileName)
            .build()

        val parser = CSVParserBuilder()
            .withSeparator(';')
            .withIgnoreQuotations(true)
            .build()

        val words = ArrayList<Word>()
        minioClient.getObject(args).use { stream ->
            val csvReader = CSVReaderBuilder(stream.reader())
                .withSkipLines(0)
                .withCSVParser(parser)
                .build()
            csvReader.forEach {
                val word = Word()
                word.categoryId = 0
                word.text = it[0]
                word.translate = it[1]
                word.example = it[2]
                words.add(word)
                log.info { "New word: ${word.text}" }
            }
        }

        if (words.isNotEmpty()) {
//            serviceWord.saveWords(words)
            log.info { "Saved ${words.size} words"}
        }

        rabbitTemplate.convertAndSend(queueName, eventMapper.changeProcess(event, "SUCCESS"))
    }
}
