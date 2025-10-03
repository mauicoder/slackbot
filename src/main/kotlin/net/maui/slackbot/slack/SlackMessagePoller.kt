package net.maui.slackbot.slack

import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import com.slack.api.model.Message
import net.maui.slackbot.config.SlackConfigProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.concurrent.atomic.AtomicReference

@Component
class SlackMessagePoller(
    private val slackGateway: SlackGateway,
    private val slackConfigProperties: SlackConfigProperties
) {

    private val logger = LoggerFactory.getLogger(SlackMessagePoller::class.java)
    private val sinkRef = AtomicReference<FluxSink<Message>>()
    private val messageFlux: Flux<Message> = Flux.create<Message>({ emitter ->
        sinkRef.set(emitter)
    }, FluxSink.OverflowStrategy.BUFFER).publish().autoConnect()

    fun messages(): Flux<Message> = messageFlux

    @Scheduled(fixedRateString = "\${slack.client.polling-interval:60000}")
    fun fetchNewMessages() {
        logger.info("retrieving messages...")
        val responseMono = slackGateway.getLatestMessages(slackConfigProperties.channel)

        responseMono.doOnNext {
            processResponse(it)
        }.doOnError { error ->
            logger.error("Error fetching messages: ${error.message}", error)
        }
            .subscribe()
    }

    private fun processResponse(response: ConversationsHistoryResponse) {
        if (response.isOk) {
            response.messages.forEach { message ->
                logger.debug("Message from ${message.user}: ${message.text}")
                sinkRef.get()?.next(message)
            }
        } else {
            logger.warn("Error fetching history: ${response.error}")
        }
    }
}
