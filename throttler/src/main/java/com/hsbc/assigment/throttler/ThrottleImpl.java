package com.hsbc.assigment.throttler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ThrottleImpl implements Throttler {

    Logger logger = LoggerFactory.getLogger(ThrottleImpl.class);

    private final ReentrantLock lock;
    private final long startTime;
    private final double stableInterval;
    private volatile long nextAvailableSlot;
    private volatile double storedPermits;
    static final double ONE_1000000000_SECOND = 1_000_000_000.0;

    public ThrottleResult shouldProceed(final int permits, final long timeout, final TimeUnit unit) throws InterruptedException {
        final long waitDuration = tryAcquireDelayDuration(permits, timeout, unit);
        logger.info("wait duration: {}", waitDuration);
        if (waitDuration < 0) {
            return ThrottleResult.DO_NOT_PROCEED;
        }
        NANOSECONDS.sleep(waitDuration);
        return ThrottleResult.PROCEED;
    }

    public long tryAcquireDelayDuration(final int permits, final long timeout, final TimeUnit unit) {
        if (timeout <= 0) {
            if (shouldProceed(permits).compareTo(ThrottleResult.PROCEED) > 0) {
                return 0;
            } else

                return -1;
        }
        final long waitDuration = unit.toNanos(timeout);
        validatePermits(permits);
        lock.lock();
        try {
            final long durationElapsed = System.nanoTime() - startTime;
            logger.info("durationElapsed while acquiring delay duration: {}", durationElapsed);
            return nextAvailableSlot - waitDuration > durationElapsed
                    ? -1 : reserveEarliestAvailable(permits, durationElapsed) - durationElapsed;
        } finally {
            lock.unlock();
        }
    }

    public ThrottleResult shouldProceed(final int permits) {
        validatePermits(permits);
        lock.lock();
        try {
            final long elapsedTime = System.nanoTime() - startTime;
            logger.info("elapsed time during the throttler request processing: {}", elapsedTime);
            if (nextAvailableSlot > elapsedTime) {
                return ThrottleResult.DO_NOT_PROCEED;
            }
            reserveEarliestAvailable(permits, elapsedTime);
            return ThrottleResult.PROCEED;
        } finally {
            lock.unlock();
        }
    }

    private long reserveEarliestAvailable(final int requiredPermits, final long elapsedTime) {
        long _nextAvailableSlot = nextAvailableSlot;
        if (elapsedTime > _nextAvailableSlot) {
            nextAvailableSlot = _nextAvailableSlot = elapsedTime;
        }
        double _storedPermits = storedPermits;
        final double storedPermitsToSpend = requiredPermits < _storedPermits ? requiredPermits : _storedPermits;
        nextAvailableSlot = peak(_nextAvailableSlot, (long) ((requiredPermits - storedPermitsToSpend) * stableInterval));
        storedPermits = _storedPermits - storedPermitsToSpend;
        logger.info("available slot : {}", _nextAvailableSlot);
        return _nextAvailableSlot;
    }

    static long peak(final long val1, final long val2) {
        final long naiveSum = val1 + val2;
        if ((val1 ^ val2) < 0 || (val1 ^ naiveSum) >= 0) {
            return naiveSum;
        }
        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1)) ^ 1);
    }

    @Override
    public void notifyWhenCanProceed(int permits) {
        validatePermits(permits);
        acquireDelayDuration(permits);
    }

    ThrottleImpl(final double permitsPerSecond, final boolean fairness) {
        if (permitsPerSecond <= 0.0 || !Double.isFinite(permitsPerSecond)) {
            throw new IllegalArgumentException("permits per second should be positive");
        }
        this.lock = new ReentrantLock(fairness);
        this.startTime = System.nanoTime();
        this.stableInterval = ONE_1000000000_SECOND / permitsPerSecond;
    }

    private static void validatePermits(final int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Requested permits " + permits + " should be positive.");
        }
    }

    public double getRate() {
        return ONE_1000000000_SECOND / stableInterval;
    }

    public long acquireDelayDuration(final int permits) {
        validatePermits(permits);
        long elapsedNanos;
        long momentAvailable;
        lock.lock();
        try {
            elapsedNanos = System.nanoTime() - startTime;
            momentAvailable = reserveEarliestAvailable(permits, elapsedNanos);
        } finally {
            lock.unlock();
        }
        return elapsedNanos >= momentAvailable ? 0 : momentAvailable - elapsedNanos;
    }


}
