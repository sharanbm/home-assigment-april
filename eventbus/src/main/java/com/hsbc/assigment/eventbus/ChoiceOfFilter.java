package com.hsbc.assigment.eventbus;

import org.checkerframework.checker.nullness.qual.Nullable;


public class ChoiceOfFilter implements EventFilter {


    @Override
    public String condition() {
        return "bonds";
    }

    @Override
    public boolean apply(@Nullable Object o) {
        return false;
    }
}
