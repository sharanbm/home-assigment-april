package com.hsbc.assigment.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public class Subscriber {

    Logger logger = LoggerFactory.getLogger(Subscriber.class);

    static Subscriber create(EventBus bus, Object listener, Method method, String SubscriberName) {
        return new Subscriber(bus, listener, method, SubscriberName);
    }

    private String subscriberName;
    private EventBus bus;
    final Object target;
    private final Method method;
    private final Executor executor;

    private Subscriber(EventBus bus, Object target, Method method, String subscriberName) {
        this.bus = bus;
        this.target = target;
        this.method = method;
        method.setAccessible(true);
        this.executor = bus.executor();
        this.subscriberName = subscriberName;
    }

    final void dispatchEvent(final Object event) {
        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            invokeSubscriberMethod(event);
                        } catch (InvocationTargetException e) {
                        }
                    }
                });
    }

    /**
     * Invokes the subscriber method. This method can be overridden to make the invocation
     * synchronized.
     */

    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        try {
            method.invoke(target, event);
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + event, e);
        } catch (IllegalAccessException e) {
            throw new Error("Method became inaccessible: " + event, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    /**
     * Gets the context for the given event.
     */

    @Override
    public final int hashCode() {
        return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Subscriber) {
            Subscriber that = (Subscriber) obj;
            return target == that.target && method.equals(that.method);
        }
        return false;
    }
}