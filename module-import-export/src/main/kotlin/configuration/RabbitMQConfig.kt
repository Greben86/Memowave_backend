package dev.greben.memowave.configuration

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMQConfig(
    @Value("\${rabbitmq.queue.name}")
    val queueName: String
) {
    /**
     * Конфигурируем очередь
     * @return Очередь RabbitMQ
     */
    @Bean
    fun queue(): Queue? {
        return Queue(queueName, false)
    }
}