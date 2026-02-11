package dev.greben.memowave.service

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import dev.greben.memowave.entities.Word
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * Сервис импортирования слов
 */
@Service
@Transactional
class ImportService(
    private val serviceWord: WordService,
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}")
    private val backet: String
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @RabbitListener(queues = ["#{@environment.getProperty('rabbitmq.queue.name')}"])
    fun receiveMessage(message: String?) {
        log.info { "Received message: $message" }
        import(message!!)
        log.info { "Finish"}
    }

    fun import(fileName: String) {
        log.info { "Point 1"}
        val args = GetObjectArgs.builder()
            .bucket(backet)
            .`object`(fileName)
            .build()

        log.info { "Point 2"}
        val parser = CSVParserBuilder()
            .withSeparator(';')
            .withIgnoreQuotations(true)
            .build()

        log.info { "Point 3"}
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

        log.info { "Point 4"}
        if (words.isNotEmpty()) {
            serviceWord.saveWords(words)
            log.info { "Saved ${words.size} words"}
        }

        log.info { "Point 5"}
        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(backet)
            .`object`(fileName)
            .build())
    }
}
