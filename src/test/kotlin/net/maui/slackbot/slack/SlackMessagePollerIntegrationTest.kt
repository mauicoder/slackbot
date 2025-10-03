import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import com.slack.api.model.Message
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import net.maui.slackbot.config.SlackConfigProperties
import net.maui.slackbot.slack.SlackGateway
import net.maui.slackbot.slack.SlackMessagePoller
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

@ExtendWith(MockKExtension::class)
class SlackMessagePollerIntegrationTest {

    @MockK
    lateinit var slackGateway: SlackGateway

    private val slackConfigProperties = SlackConfigProperties(
        url = "https://slack.com/api/",
        token = "xoxb-fake-token",
        channel = "C123",
        pollingInterval = 1000
    )

    private lateinit var poller: SlackMessagePoller

    @BeforeEach
    fun setUp() {
        poller = SlackMessagePoller(slackGateway, slackConfigProperties)
    }

@Test
fun `messages flux emits new messages after fetchNewMessages`() {
    val response = ConversationsHistoryResponse().apply {
        isOk = true
        messages = listOf(
            Message().apply { user = "U1"; text = "Hello" },
            Message().apply { user = "U2"; text = "World" }
        )
    }
    every { slackGateway.getLatestMessages(any()) } returns Mono.just(response)

    val messageFlux = poller.messages()

    StepVerifier.create(messageFlux)
        .then { poller.fetchNewMessages() } // Ensures sink is initialized before pushing
        .expectNextMatches { it.user == "U1" && it.text == "Hello" }
        .expectNextMatches { it.user == "U2" && it.text == "World" }
        .thenCancel()
        .verify(Duration.ofSeconds(2))
}
    @Test
    fun `messages flux does not emit when response is not ok`() {
        val response = ConversationsHistoryResponse().apply {
            isOk = false
            error = "channel_not_found"
        }
        every { slackGateway.getLatestMessages(any()) } returns Mono.just(response)

        val messageFlux = poller.messages()

        poller.fetchNewMessages()

        StepVerifier.create(messageFlux)
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(100))
            .thenCancel()
            .verify()
    }
}
