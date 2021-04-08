package com.hsbc.assigment.eventbus;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

abstract class Dispatcher {

    Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    static Dispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher();
    }

    abstract void dispatch(Object event, Iterator<Subscriber> subscribers);

    private static final class PerThreadQueuedDispatcher extends Dispatcher {


        private final ThreadLocal<Queue<Event>> queue =
                new ThreadLocal<Queue<Event>>() {
                    @Override
                    protected Queue<Event> initialValue() {
                        return new ArrayDeque<>();
                    }
                };

        private final ThreadLocal<Boolean> dispatching =
                new ThreadLocal<Boolean>() {
                    @Override
                    protected Boolean initialValue() {
                        return false;
                    }
                };

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            logger.info("inside the dispatch method to publish the event");
            Queue<Event> queueForThread = queue.get();
            queueForThread.offer(new Event(event, subscribers));
            if (!dispatching.get()) {
                dispatching.set(true);
                try {
                    Event nextEvent;
                    while ((nextEvent = queueForThread.poll()) != null) {
                        while (nextEvent.subscribers.hasNext()) {
                            nextEvent.subscribers.next().dispatchEvent(nextEvent.event);
                        }
                    }
                } finally {
                    dispatching.remove();
                    queue.remove();
                }
            }
        }

        private static final class Event {
            private final Object event;
            private final Iterator<Subscriber> subscribers;

            private Event(Object event, Iterator<Subscriber> subscribers) {
                this.event = event;
                this.subscribers = subscribers;
            }
        }
    }
}
