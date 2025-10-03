package net.maui.slackbot.slack

import com.slack.api.Slack
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import net.maui.slackbot.config.SlackConfigProperties
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import org.springframework.stereotype.Component

@Component
class SlackGateway(
    private val slackClient: Slack,
    private val slackProperties: SlackConfigProperties) {

    private val logger = LoggerFactory.getLogger(SlackGateway::class.java)


    fun getLatestMessages(channelId: String, limit: Int = 10 ): Mono<ConversationsHistoryResponse> {
        return Mono.fromCallable {
            // This is the blocking call from the Slack SDK
            slackClient.methods(slackProperties.token).conversationsHistory { req ->
                req.channel(channelId).limit(limit) // Get last limit messages
            }
        }
            // IMPORTANT: Offload the blocking call to a dedicated thread pool
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext { response ->
                if (!response.isOk) {
                    // Handle API error
                    logger.error("Slack API Error: ${response.error}")
                    error(response.error)
                }
            }.doOnError { Mono.empty<ConversationsHistoryResponse>() }
    }
}