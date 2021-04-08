package com.hsbc.assigment.eventbus;

import com.google.common.collect.Iterators;
import com.hsbc.assigment.eventbus.helper.StringSubscriber;
import junit.framework.TestCase;

public class SubscriberMapTests extends TestCase {

    private final SubscribersMap subscribersMap = new SubscribersMap(new EventBusImpl());

    private EventBus bus;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bus = new EventBusImpl();
    }

    public void testAddSubscribers() {


        assertEquals(0, subscribersMap.getSubscribersForTesting(String.class).size());

        subscribersMap.addSubscriber(new StringSubscriber());
        assertEquals(1, subscribersMap.getSubscribersForTesting(String.class).size());

        subscribersMap.addSubscriber(new StringSubscriber());
        assertEquals(2, subscribersMap.getSubscribersForTesting(String.class).size());


        assertEquals(2, subscribersMap.getSubscribersForTesting(String.class).size());

    }

    public void testGetSubscribers() {
        assertEquals(0, Iterators.size(subscribersMap.getSubscribers("")));

        subscribersMap.addSubscriber(new StringSubscriber());
        assertEquals(1, Iterators.size(subscribersMap.getSubscribers("")));

        subscribersMap.addSubscriber(new StringSubscriber());
        assertEquals(2, Iterators.size(subscribersMap.getSubscribers("")));
    }

}
