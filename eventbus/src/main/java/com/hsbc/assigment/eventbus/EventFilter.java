package com.hsbc.assigment.eventbus;

import com.google.common.base.Predicate;

public interface EventFilter extends Predicate<Object> {

    //filter by
    public String condition();


}
