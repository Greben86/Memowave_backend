package dev.greben.memowave.service

import dev.greben.memowave.entities.User
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

/**
 * Сервис рассылки сообщений электронной почты
 */
@Service
class EmailNotificationService(
    private val emailSender: JavaMailSender
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    val SUBJECT: String = "OTP код"

    /**
     * Отправка OTP кода
     *
     * @param user пользователь
     * @param otpCode OTP код
     */
    fun sendOtpCode(email: String, otpCode: String): Boolean =
        try {
            val simpleMailMessage = SimpleMailMessage()
            simpleMailMessage.setTo(email)
            simpleMailMessage.subject = SUBJECT
            simpleMailMessage.text = "Memowave OTP: $otpCode"
            emailSender.send(simpleMailMessage)

            log.info { "Сообщение Email отправлено успешно" }

            true
        } catch (e: Exception) {
            error { "Ошибка отправки Email: ${e.message}" }
            false
        }
}