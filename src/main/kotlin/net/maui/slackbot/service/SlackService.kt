package net.maui.slackbot.service

import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import net.maui.slackbot.slack.SlackGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class SlackService(
    private val slackGateway: SlackGateway) {

    fun getLatestMessages(channelId: String): Mono<ConversationsHistoryResponse> {
        return slackGateway.getLatestMessages(channelId, 10)
    }
}