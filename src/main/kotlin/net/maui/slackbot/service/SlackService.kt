package net.maui.slackbot.service

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import net.maui.slackbot.config.SlackConfigProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


@Service
class SlackService(
    private val slackClient: Slack,
    private val slackProperties: SlackConfigProperties) {

    private val logger = LoggerFactory.getLogger(SlackService::class.java)

    fun getLatestMessages(): Flux<String> {
        val slack = Slack.getInstance()
        val methods: MethodsClient = slack.methods(slackProperties.token)
        val result = methods.conversationsList {
            r ->
                r.token(System.getenv(slackProperties.token)) // The token you used to initialize your app
        }

        return Flux.fromIterable<String>(
            result.channels.map
            { "Found conversation ID: ${it.id} in channel: ${it.name}" }
        ).log()
    }

    fun getLatestMessages(channelId: String): Mono<ConversationsHistoryResponse> {
        return Mono.fromCallable {
            // This is the blocking call from the Slack SDK
            slackClient.methods(slackProperties.token).conversationsHistory { req ->
                req.channel(channelId).limit(10) // Get last 10 messages
            }
        }
            // IMPORTANT: Offload the blocking call to a dedicated thread pool
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext { response ->
                if (!response.isOk) {
                    // Handle API error
                    println("Slack API Error: ${response.error}")
                }
            }
    }
}