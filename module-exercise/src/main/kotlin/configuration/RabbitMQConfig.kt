package dev.greben.memowave.configuration

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableRabbit
@Configuration
@EnableConfigurationProperties(RabbitQueueProperties::class)
class RabbitMQConfig(
    val queueProperties: RabbitQueueProperties
) {
    /**
     * Конфигурируем исходящую очередь
     * @return Очередь RabbitMQ
     */
    @Bean
    fun outputQueue(): Queue = Queue(queueProperties.output, false)

    /**
     * Конфигурируем входящую очередь
     * @return Очередь RabbitMQ
     */
    @Bean
    fun inputQueue(): Queue = Queue(queueProperties.input, false)

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