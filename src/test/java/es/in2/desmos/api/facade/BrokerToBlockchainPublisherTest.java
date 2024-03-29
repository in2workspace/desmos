package es.in2.desmos.api.facade;

import es.in2.desmos.api.facade.impl.BrokerToBlockchainPublisherImpl;
import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.model.EventQueuePriority;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.blockchain.service.DLTAdapterEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BrokerToBlockchainPublisherTest {

    // Test data
    private final String processId = "testProcessId";
    private final BrokerNotification brokerNotification = BrokerNotification.builder().id("id")
            .type("type").data(List.of(Map.of("key", "value"))).subscriptionId("subscriptionId")
            .notifiedAt("notifiedAt").build();
    private final BlockchainEvent blockchainEvent = BlockchainEvent.builder()
            .eventType("eventType").organizationId("organizationId").entityId("entityId")
            .previousEntityHash("previousEntityHash").dataLocation("dataLocation")
            .metadata(List.of("metadata1", "metadata2")).build();
    @Mock
    private NotificationProcessorService notificationProcessorService;
    @Mock
    private BlockchainEventCreatorService blockchainEventCreatorService;
    @Mock
    private DLTAdapterEventPublisher DLTAdapterEventPublisher;
    @Mock
    private QueueService brokerToBlockchainQueueService;
    @InjectMocks
    private BrokerToBlockchainPublisherImpl brokerToBlockchainPublisher;

    @Test
    void testProcessAndPublishBrokerNotificationToBlockchain_Success() {
        // Arrange
        HashMap<String, Object> dataMap = new HashMap<>();
        // Mock the behavior of services
        when(notificationProcessorService.processBrokerNotification(anyString(), any()))
                .thenReturn(Mono.just(dataMap));
        when(blockchainEventCreatorService.createBlockchainEvent(anyString(), any()))
                .thenReturn(Mono.just(blockchainEvent));
        when(DLTAdapterEventPublisher.publishBlockchainEvent(anyString(), any()))
                .thenReturn(Mono.empty());
        // Act
        brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(processId, brokerNotification).block();
        // Assert - Verify that services are called with the expected parameters
        verify(notificationProcessorService).processBrokerNotification(processId, brokerNotification);
        verify(blockchainEventCreatorService).createBlockchainEvent(processId, dataMap);
        verify(DLTAdapterEventPublisher).publishBlockchainEvent(processId, blockchainEvent);
    }

    @Test
    void testStartProcessingEventsSuccessfulFlow_broker() {
        HashMap<String, Object> dataMap = new HashMap<>();
        // Mock the event stream with a single event
        EventQueue eventQueue = new EventQueue(List.of(brokerNotification), EventQueuePriority.PUBLICATION);
        when(brokerToBlockchainQueueService.getEventStream())
                .thenReturn(Flux.just(eventQueue));

        when(notificationProcessorService.processBrokerNotification(processId, brokerNotification))
                .thenReturn(Mono.just(dataMap));
        when(blockchainEventCreatorService.createBlockchainEvent(processId, dataMap))
                .thenReturn(Mono.just(blockchainEvent));
        when(DLTAdapterEventPublisher.publishBlockchainEvent(processId, blockchainEvent))
                .thenReturn(Mono.empty());

        // Mock downstream services for successful execution
        when(brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(processId, brokerNotification))
                .thenReturn(Mono.empty()); // Simulate successful completion



        // Act
        StepVerifier.create(brokerToBlockchainPublisher.startProcessingEvents())
                .verifyComplete();
    }


    @Test
    void testProcessAndPublishBrokerNotificationToBlockchain_Error() {
        // Simulate an error in the NotificationProcessorService
        when(notificationProcessorService.processBrokerNotification(anyString(), any()))
                .thenReturn(Mono.error(new RuntimeException("Test Processing Error")));
        // No need to mock other services as the error will prevent their execution
        // Execute the method and handle the error to prevent test failure
        brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(processId, brokerNotification)
                .onErrorResume(e -> Mono.empty()) // Handle error to prevent test failure
                .block();
        // Verify that the first service is called and others are not due to the error
        verify(notificationProcessorService).processBrokerNotification(processId, brokerNotification);
        verifyNoInteractions(blockchainEventCreatorService);
        verifyNoInteractions(DLTAdapterEventPublisher);
        // Verify error logging - This part is tricky as Mockito doesn't directly support verifying log statements.
        // You may need to use additional tools or frameworks to assert log output, or alternatively,
        // verify that the subsequent steps after the error are not executed, as done here.
    }

}
