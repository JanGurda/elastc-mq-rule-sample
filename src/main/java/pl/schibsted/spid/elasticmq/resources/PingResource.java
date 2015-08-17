package pl.schibsted.spid.elasticmq.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import pl.schibsted.spid.elasticmq.server.ElasticMqRuleSampleApplicationConfiguration;

@Path("/ping")
@Consumes(MediaType.APPLICATION_JSON)
public class PingResource {

    private final AmazonSQSClient client;
    private final String queueUrl;

    public PingResource(ElasticMqRuleSampleApplicationConfiguration configuration) {
        // Instantiate AmazonSQSClient
        this.client = new AmazonSQSClient(new BasicAWSCredentials(configuration.getAwsAccessKey(), configuration.getAwsSecretKey()));
        this.queueUrl = configuration.getQueueUrl();
    }

    @POST
    public void ping(@NotNull String pingBody) {
        String toSend = processPingBody(pingBody);
        // sendMessage method is thread safe.
        // Send message with given body to queue.
        client.sendMessage(queueUrl, toSend);
    }

    private String processPingBody(String pingBody) {
        // Simulate very complicated message processing.
        return pingBody.toUpperCase();
    }
}
