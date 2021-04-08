package com.hsbc.assigment.eventbus;


import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

final class SubscribersMap {

    Logger logger = LoggerFactory.getLogger(SubscribersMap.class);

    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers =
            new ConcurrentHashMap<>();


    private final EventBus bus;

    SubscribersMap(EventBusImpl bus) {
        this.bus = bus;
    }

    void addSubscriber(Object listener) {
        Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);

        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
            Class<?> eventType = entry.getKey();
            logger.info("entry.getkey():  {}", eventType.getName());
            Collection<Subscriber> eventMethodsInListener = entry.getValue();
            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
            if (eventSubscribers == null) {
                CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
                eventSubscribers =
                        MoreObjects.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
            }
            eventSubscribers.addAll(eventMethodsInListener);
            logger.info("eventSubscribers to string {}", eventSubscribers);
        }
    }

    void addSubscriber(@Nullable EventFilter eventFilter, Object listener) {
        Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);
        eventFilter = new ChoiceOfFilter();
        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> eventMethodsInListener = entry.getValue();

            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

            if (eventSubscribers == null) {
                CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
                eventSubscribers =
                        MoreObjects.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
            }
            eventSubscribers.addAll(eventMethodsInListener);
            eventSubscribers.stream().filter(eventFilter);
            logger.info("filtered eventSubscribers to string {}", eventSubscribers);
        }
    }

    private Multimap<Class<?>, Subscriber> findAllSubscribers(Object listener) {
        Multimap<Class<?>, Subscriber> methodsInListener = HashMultimap.create();
        Class<?> clazz = listener.getClass();
        for (Method method : getAnnotatedMethodsNotCached(clazz)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];
            methodsInListener.put(eventType, Subscriber.create(bus, listener, method, listener.toString()));
        }
        return methodsInListener;
    }

    Iterator<Subscriber> getSubscribers(Object event) {
        ImmutableSet<Class<?>> eventTypes = ImmutableSet.<Class<?>>copyOf(
                TypeToken.of(event.getClass()).getTypes().rawTypes());

        List<Iterator<Subscriber>> subscriberIterators = new ArrayList<>(eventTypes.size());

        for (Class<?> eventType : eventTypes) {
            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
            if (eventSubscribers != null) {
                subscriberIterators.add(eventSubscribers.iterator());
            }
        }
        return Iterators.concat(subscriberIterators.iterator());
    }

    Set<Subscriber> getSubscribersForTesting(Class<?> eventType) {
        return MoreObjects.firstNonNull(subscribers.get(eventType), ImmutableSet.<Subscriber>of());
    }

    private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
        Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
        Map<MethodIdentifier, Method> identifiers = Maps.newHashMap();
        for (Class<?> supertype : supertypes) {
            for (Method method : supertype.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                    MethodIdentifier identifier = new MethodIdentifier(method);
                    if (!identifiers.containsKey(identifier)) {
                        identifiers.put(identifier, method);
                    }
                }
            }
        }
        return ImmutableList.copyOf(identifiers.values());
    }
}
