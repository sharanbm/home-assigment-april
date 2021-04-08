package com.hsbc.assigment.eventbus;

import java.util.concurrent.Executor;

public interface EventBus {

    // Feel free to replace Object with something more specific,
    // but be prepared to justify it
    void publishEvent(Object event);

    // How would you denote the subscriber?
    void addSubscriber(Object object);


    // Would you allow clients to filter the events they receive? How would the interface look like?
    void addSubscriberForFilteredEvents(EventFilter eventFilter, Object subscriber);

    Executor executor();


}


