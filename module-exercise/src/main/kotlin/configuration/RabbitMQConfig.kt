package dev.greben.memowave.configuration

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableRabbit
class RabbitMQConfig(
    @Value("\${rabbitmq.queue.output}")
    val outputQueueName: String,
    @Value("\${rabbitmq.queue.input}")
    val inputQueueName: String
) {
    /**
     * Конфигурируем исходящую очередь
     * @return Очередь RabbitMQ
     */
    @Bean
    fun outputQueue(): Queue = Queue(outputQueueName, false)

    /**
     * Конфигурируем входящую очередь
     * @return Очередь RabbitMQ
     */
    @Bean
    fun inputQueue(): Queue = Queue(inputQueueName, false)

    @Bean
    fun jsonMessageConverter(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = jsonMessageConverter()
        return template
    }

    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory?): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(jsonMessageConverter())
        return factory
    }
}