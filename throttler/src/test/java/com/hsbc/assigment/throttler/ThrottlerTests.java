package com.hsbc.assigment.throttler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ThrottlerTests {

    static Throttler throttler;

    @BeforeAll
    public static void warmup_throttler() {

        throttler = new ThrottleImpl(10,true);
    }

    @Test
    void testShouldProceed() throws InterruptedException {

        assertEquals(Throttler.ThrottleResult.PROCEED, throttler.shouldProceed(3,1, TimeUnit.NANOSECONDS));
        assertEquals(Throttler.ThrottleResult.DO_NOT_PROCEED, throttler.shouldProceed(31,1, TimeUnit.NANOSECONDS));
    }


    @Test
    void testAcquireParameterValidation() {
        final Throttler throttle = Throttler.create(999);

        assertThrows(IllegalArgumentException.class, () -> throttle.shouldProceed(0));
        assertThrows(IllegalArgumentException.class, () -> throttle.shouldProceed(-1));
    }

}
