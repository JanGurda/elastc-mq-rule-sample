package pl.schibsted.spid.elasticmq.server;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import pl.schibsted.spid.elasticmq.resources.PingResource;

public class ElasticMqRuleSampleApplication extends Application<ElasticMqRuleSampleApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new ElasticMqRuleSampleApplication().run(args);
    }

    @Override
    public void run(ElasticMqRuleSampleApplicationConfiguration configuration, Environment environment) throws Exception {
        PingResource resource = new PingResource(configuration);
        environment.jersey().register(resource);
    }

}
