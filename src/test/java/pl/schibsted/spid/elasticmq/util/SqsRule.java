package pl.schibsted.spid.elasticmq.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.elasticmq.NodeAddress;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqsRule implements TestRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsRule.class);

    // Reference to ElasticMQ server
    private static SQSRestServer server;
    private Map<String, SqsQueue> queues = new ConcurrentHashMap<>();
    private SqsRuleConfiguration configuration;

    public SqsRule(SqsRuleConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Statement apply(Statement childStatement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    setup();
                    childStatement.evaluate();
                } finally {
                    shutdown();
                }
            }
        };
    }

    private synchronized void setup() {
        // Start ElasticMQ in embedded mode.
        server = SQSRestServerBuilder.withPort(configuration.getPort())
                .withServerAddress(new NodeAddress("http", "localhost", configuration.getPort(), "")).start();
        LOGGER.info("SQS server started on port " + configuration.getPort());
        for (String queueName : configuration.getQueues()) {
            // Use standard ElasticMQ credentials ("x", "x")
            AmazonSQSClient amazonSQSClient = new AmazonSQSClient(new BasicAWSCredentials("x", "x"));
            // ElasticMQ is running on the same machine as integration test
            String endpoint = "http://localhost:" + configuration.getPort();
            amazonSQSClient.setEndpoint(endpoint);
            // Create queue with given name
            amazonSQSClient.createQueue(queueName);
            // Queue URL in ElasticMQ is http://host:port/queue/{queue_name}
            queues.put(queueName, new SqsQueue(amazonSQSClient, endpoint + "/queue/" + queueName));
        }
    }

    private synchronized void shutdown() {
        server.stopAndWait();
    }

    public SqsQueue getQueue(String queueName) {
        return queues.get(queueName);
    }

    public void purgeAllQueues() {
        // Cleans
        for (String queueName : queues.keySet()) {
            queues.get(queueName).purge();
        }
    }
}
