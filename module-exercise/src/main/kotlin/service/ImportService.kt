package dev.greben.memowave.service

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import dev.greben.memowave.entities.Word
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
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

    fun import(fileName: String) {
        val args = GetObjectArgs.builder()
            .bucket(backet)
            .`object`(fileName)
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
                word.text = it[0]
                word.translate = it[1]
                word.example = it[2]
                words.add(word)
            }
        }

        if (words.isNotEmpty()) {
            serviceWord.saveWords(words)
        }

        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(backet)
            .`object`(fileName)
            .build())
    }
}
