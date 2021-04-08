package com.hsbc.assigment.eventbus.helper;

import com.hsbc.assigment.eventbus.Subscribe;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class HelloEvent {
    private List<String> events = new ArrayList<>();

    @Subscribe
    public void hereHaveAString(String string) {
        events.add(string);
    }

    public void methodWithoutAnnotation(String string) {
        Assert.fail("Event bus must not call methods without @Subscribe!");
    }

    public List<String> getEvents() {
        return events;
    }
}
