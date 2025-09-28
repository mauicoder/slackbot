package net.maui.slackbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
// Scan the package where your config lives
@ConfigurationPropertiesScan("net.maui.slackbot.config")
@EnableScheduling
class SlackBotApplication

fun main(args: Array<String>) {
	runApplication<SlackBotApplication>(*args)
}

