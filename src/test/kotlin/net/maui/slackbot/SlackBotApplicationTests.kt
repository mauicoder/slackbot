package net.maui.slackbot

import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ConfigurationPropertiesScan("net.maui.slackbot.config")
class SlackBotApplicationTests {

	@Test
	fun contextLoads() {
	}

}
