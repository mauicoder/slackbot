package net.maui.slackbot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "slack.client")
data class SlackConfigProperties(
    val url: String,
    val token: String,
    val channel: String,
    val pollingInterval: Int
)