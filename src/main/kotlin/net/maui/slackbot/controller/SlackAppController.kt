package net.maui.slackbot.controller

import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import net.maui.slackbot.service.SlackService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/messages")
class SlackAppController(val slackMessageService: SlackService) {


    /**
     * Retrieves the latest messages from a given Slack channel ID.
     * The response is wrapped in a Mono to align with the reactive nature of WebFlux.
     * * @param channelId The ID of the Slack channel (e.g., C1234567890).
     * @return A Mono that will eventually contain the Slack API response object.
     */
    @GetMapping("/latest")
    fun getLatestChannelMessages(
        @RequestParam("channelId") channelId: String
    ): Mono<ConversationsHistoryResponse> {

        // The controller simply delegates the call to the service.
        // The service already handles offloading the blocking Slack SDK call
        // to a dedicated thread (Schedulers.boundedElastic()).
        return slackMessageService.getLatestMessages(channelId)
    }

}