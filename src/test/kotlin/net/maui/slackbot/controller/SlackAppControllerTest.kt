import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import com.slack.api.model.Message
import net.maui.slackbot.slack.SlackGateway
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(classes = [net.maui.slackbot.SlackBotApplication::class])
@AutoConfigureWebTestClient
class SlackAppControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockitoBean
    lateinit var slackGateway: SlackGateway

    @Test
    fun `getLatestChannelMessages returns ConversationsHistoryResponse`() {
        val channelId = "C1234567890"
        val response = ConversationsHistoryResponse().apply {
            isOk = true
            messages = listOf(
                Message().apply { text = "Hello" },
                Message().apply { text = "World" }
            )
        }
        Mockito.`when`(slackGateway.getLatestMessages(channelId)).thenReturn(Mono.just(response))

        webTestClient.get()
            .uri("/messages/latest?channelId=$channelId")
            .exchange()
            .expectStatus().isOk
            .expectBody(ConversationsHistoryResponse::class.java)
            .value { resp ->
                assert(resp.messages.size == 2)
                assert(resp.messages.map { it.text } == listOf("Hello", "World"))
            }
    }
}