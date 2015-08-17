package pl.schibsted.spid.elasticmq.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class ElasticMqRuleSampleApplicationConfiguration extends Configuration {

    @NotEmpty
    private String queueUrl;
    @NotEmpty
    private String awsAccessKey;
    @NotEmpty
    private String awsSecretKey;

    @JsonProperty
    public String getQueueUrl() {
        return queueUrl;
    }

    @JsonProperty
    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    @JsonProperty
    public String getAwsSecretKey() {
        return awsSecretKey;
    }
}
