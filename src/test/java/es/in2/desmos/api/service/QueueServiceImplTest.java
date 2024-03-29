package es.in2.desmos.api.service;

import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.service.impl.QueueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.concurrent.PriorityBlockingQueue;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class QueueServiceImplTest {

    @Mock
    private Sinks.Many<EventQueue> mockSink;

    @InjectMocks
    private QueueServiceImpl queueService;

    @Mock
    private PriorityBlockingQueue<EventQueue> mockQueue;

    private EventQueue event;

    @BeforeEach
    void setUp() {
        event = new EventQueue();
    }

    @Test
    void enqueueEventTest() {
        StepVerifier.create(queueService.enqueueEvent(event))
                .verifyComplete();

    }

    @Test
    void unboundedQueueTest() throws IllegalAccessException, NoSuchFieldException {
        when(mockQueue.offer(event)).thenReturn(false);
        Field field = QueueServiceImpl.class.getDeclaredField("queue");
        field.setAccessible(true);
        field.set(queueService, mockQueue);
        StepVerifier.create(queueService.enqueueEvent(event))
                .verifyComplete();
    }
}