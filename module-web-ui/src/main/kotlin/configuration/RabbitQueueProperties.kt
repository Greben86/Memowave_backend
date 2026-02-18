package dev.greben.memowave.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("rabbitmq.queue")
data class RabbitQueueProperties @ConstructorBinding constructor(
    var input: String?,
    var output: String?
)