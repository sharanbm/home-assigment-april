package com.hsbc.assigment.eventbus;

import com.hsbc.assigment.eventbus.helper.HelloEvent;
import junit.framework.TestCase;

import java.util.List;

public class EventBusTest extends TestCase {

    private static final String EVENT = "Hello";

    private EventBus bus;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bus = new EventBusImpl();
    }

    public void testBasicCatcherDistribution() {
        HelloEvent catcher = new HelloEvent();
        bus.addSubscriber(catcher);
        bus.publishEvent(EVENT);

        List<String> events = catcher.getEvents();
        assertEquals("Only one event should be delivered.", 1, events.size());
        assertEquals("Correct string should be delivered.", EVENT, events.get(0));
    }
}
