package es.in2.desmos.api.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventQueueTest {

    @Test
    void compareToShouldReturnNegativeWhenThisHasHigherPriority() {
        EventQueue highPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.SYNCHRONIZATION)
                .build();
        EventQueue lowPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();

        assertTrue(highPriorityEvent.compareTo(lowPriorityEvent) < 0, "High priority should be 'less' than low priority");
    }

    @Test
    void compareToShouldReturnPositiveWhenThisHasLowerPriority() {
        EventQueue lowPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();
        EventQueue highPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.SYNCHRONIZATION)
                .build();

        assertTrue(lowPriorityEvent.compareTo(highPriorityEvent) > 0, "Low priority should be 'greater' than high priority");
    }

    @Test
    void compareToShouldReturnZeroWhenPrioritiesAreEqual() {
        EventQueue firstMediumPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();
        EventQueue secondMediumPriorityEvent = EventQueue.builder()
                .priority(EventQueuePriority.PUBLICATION)
                .build();

        assertEquals(0, firstMediumPriorityEvent.compareTo(secondMediumPriorityEvent), "Equal priorities should result in " +
                "compareTo returning zero");
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        List<Object> event1 = Arrays.asList("event1", "event2");
        EventQueuePriority priority1 = EventQueuePriority.SYNCHRONIZATION;
        EventQueue eventQueue1 = EventQueue.builder()
                .event(event1)
                .priority(priority1)
                .build();

        List<Object> event2 = Arrays.asList("event1", "event2");
        EventQueuePriority priority2 = EventQueuePriority.SYNCHRONIZATION;
        EventQueue eventQueue2 = EventQueue.builder()
                .event(event2)
                .priority(priority2)
                .build();

        // Assert
        assertEquals(eventQueue1, eventQueue2, "Expected eventQueue1 to be equal to eventQueue2");
        assertEquals(eventQueue1.hashCode(), eventQueue2.hashCode(), "Expected hash codes to be equal");
    }

    @Test
    void testSetEvent() {
        // Arrange
        EventQueue eventQueue = new EventQueue();
        List<Object> event = Arrays.asList("event1", "event2");

        // Act
        eventQueue.setEvent(event);

        // Assert
        assertEquals(event, eventQueue.getEvent(), "Expected the event to be set correctly");
    }

    @Test
    void testSetPriority() {
        // Arrange
        EventQueue eventQueue = new EventQueue();
        EventQueuePriority priority = EventQueuePriority.PUBLICATION;

        // Act
        eventQueue.setPriority(priority);

        // Assert
        assertEquals(priority, eventQueue.getPriority(), "Expected the priority to be set correctly");
    }

    @Test
    void testCanEqual() {
        // Arrange
        EventQueue eventQueue1 = new EventQueue();
        EventQueue eventQueue2 = new EventQueue();

        // Assert
        assertTrue(eventQueue1.canEqual(eventQueue2), "Expected canEqual to return true");
    }

    @Test
    void testEventQueueBuilderToString() {
        // Arrange
        List<Object> event = Arrays.asList("event1", "event2");
        EventQueuePriority priority = EventQueuePriority.SYNCHRONIZATION;

        String expectedToString = "EventQueue.EventQueueBuilder(event=" + event
                + ", priority=" + priority + ")";

        // Act
        EventQueue.EventQueueBuilder eventQueueBuilder = EventQueue.builder()
                .event(event)
                .priority(priority);

        // Assert
        assertEquals(expectedToString, eventQueueBuilder.toString());
    }

}