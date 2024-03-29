package es.in2.desmos.broker.util;

import es.in2.desmos.broker.adapter.OrionLdAdapter;
import es.in2.desmos.broker.adapter.ScorpioAdapter;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import es.in2.desmos.broker.service.GenericBrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerFactory {

    private final BrokerProperties brokerPathProperties;
    private final ScorpioAdapter scorpioAdapter;
    private final OrionLdAdapter orionLdAdapter;

    public GenericBrokerService getBrokerAdapter() {
        return switch (brokerPathProperties.provider()) {
            case "scorpio" -> scorpioAdapter;
            case "orion-ld" -> orionLdAdapter;
            default -> throw new IllegalArgumentException("Invalid IAM provider: " + brokerPathProperties.provider());
        };
    }

}
