package net.maui.slackbot.config

import com.slack.api.Slack
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlackBotConfig {

    @Bean
    fun slackClient(slackConfig: SlackConfigProperties): Slack {
        // This is the core Slack API client. It is safe to use in a WebFlux app
        // for outbound calls like getting messages.
        //val config = SlackConfig() //TODO override the bse URL
        return Slack.getInstance()
    }

}