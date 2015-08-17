package pl.schibsted.spid.elasticmq.util;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class SqsRuleConfiguration {

    private int port;

    @Singular
    private Set<String> queues;
}
