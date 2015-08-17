package pl.schibsted.spid.elasticmq.resources;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.sqs.model.Message;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import pl.schibsted.spid.elasticmq.server.ElasticMqRuleSampleApplication;
import pl.schibsted.spid.elasticmq.server.ElasticMqRuleSampleApplicationConfiguration;
import pl.schibsted.spid.elasticmq.util.SqsRule;
import pl.schibsted.spid.elasticmq.util.SqsRuleConfiguration;

import static org.junit.Assert.assertEquals;

public class ITestPingResource {

    @ClassRule
    public static DropwizardAppRule<ElasticMqRuleSampleApplicationConfiguration> app =
            new DropwizardAppRule<>(ElasticMqRuleSampleApplication.class,
                    ITestPingResource.class.getClassLoader().getResource("test.yml").getPath());

    @ClassRule
    public static SqsRule sqs = new SqsRule(SqsRuleConfiguration.builder()
            .queue("sample-queue").port(8888).build());

    private Client client = ClientBuilder.newClient();

    @After
    public void tearDown() {
        sqs.purgeAllQueues();
    }

    @Test
    public void shouldPublishProcessedRequestPayload() throws Exception {
        // given
        String toSend = "abcdefgh";
        // when
        Response response = client
                .target("http://127.0.0.1:" + app.getLocalPort() + "/ping")
                .request().post(Entity.json(toSend));
        // then
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
        List<Message> messagesFromQueue = sqs.getQueue("sample-queue").read(10);
        assertEquals(1, messagesFromQueue.size());
        assertEquals("ABCDEFGH", messagesFromQueue.get(0).getBody());
    }
}
