package com.hsbc.assigment.eventbus;


import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class EventBusImpl implements EventBus {

    Logger logger = LoggerFactory.getLogger(EventBusImpl.class);

    private final SubscribersMap subscribers = new SubscribersMap(this);
    private final Dispatcher dispatcher;
    private Executor executor;

    public EventBusImpl() {
        this(MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue());
    }

    EventBusImpl(Executor executor, Dispatcher dispatcher) {
        this.executor = executor;
        this.dispatcher = dispatcher;
    }


    @Override
    public void publishEvent(Object event) {
        Iterator<Subscriber> eventSubscribers = subscribers.getSubscribers(event);
        logger.info("publishing the event : <{}> ", event);
        if (eventSubscribers.hasNext()) {
            dispatcher.dispatch(event, eventSubscribers);
        }
    }

    @Override
    public void addSubscriber(Object object) {
        subscribers.addSubscriber(object);
    }

    @Override
    public void addSubscriberForFilteredEvents(@Nullable EventFilter eventFilter, Object subscriber) {
        subscribers.addSubscriber(eventFilter, subscriber);
    }

    public final Executor executor() {

        return executor;
    }

}
