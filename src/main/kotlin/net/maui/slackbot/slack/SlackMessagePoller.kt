package net.maui.slackbot.slack

import com.slack.api.Slack
import net.maui.slackbot.config.SlackConfigProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SlackMessagePoller(
    private val slack: Slack, // You'd need to create this Bean or use getInstance()
    private val slackConfigProperties: SlackConfigProperties
) {

    private val logger = LoggerFactory.getLogger(SlackMessagePoller::class.java)

    @Scheduled(fixedRateString = "\${slack.client.polling-interval:60000}")
    fun fetchNewMessages() {
        logger.info("retrieving messages...")
        val response = slack.methods(slackConfigProperties.token).conversationsHistory {
            it.channel(slackConfigProperties.channel)
                .limit(10) // Get the last 10 messages
        }

        if (response.isOk) {
            response.messages.forEach { message ->
                logger.debug("Message from ${message.user}: ${message.text}")
            }
        } else {
            logger.warn("Error fetching history: ${response.error}")
        }
    }
}