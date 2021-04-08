package com.hsbc.assigment.throttler;

import java.util.concurrent.TimeUnit;

public interface Throttler {


    // check if we can proceed (poll)
    ThrottleResult shouldProceed(int permits);

    ThrottleResult shouldProceed(int permits, long timeout, TimeUnit timeUnit) throws InterruptedException;

    // subscribe to be told when we can proceed (Push)
    void notifyWhenCanProceed(int permits);

    enum ThrottleResult {
        PROCEED, // publish, aggregate etc
        DO_NOT_PROCEED //
    }

    static Throttler create(final double permitsPerSecond, final boolean fairness) {
        return new ThrottleImpl(permitsPerSecond, fairness);
    }

    static Throttler create(final double permitsPerSecond) {
        return create(permitsPerSecond, false);
    }

}
