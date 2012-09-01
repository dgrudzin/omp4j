package ru.unn.omp4j.directives.workshare;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.OMPRuntimeException;
import ru.unn.omp4j.util.CollectionUtils;
import ru.unn.omp4j.util.LoggingUtils;

public abstract class OrderedLock {

    private static final Log log = LogFactory.getLog(OrderedLock.class);

    protected int firstIndex;

    protected int lastIndex;

    protected int step;

    protected final AtomicInteger count = new AtomicInteger();

    private final Map<Integer, Condition> conditions = CollectionUtils.newConcurrentHashMap();

    private final Lock lock = new ReentrantLock();

    public static OrderedLock getInstance(int firstIndex, int lastIndex, int step) {
        return lastIndex > firstIndex ? new DirectOrderedLock(firstIndex, lastIndex, step) : new InverseOrderedLock(
                firstIndex, lastIndex, step);
    }

    protected OrderedLock(int firstIndex, int lastIndex, int step) {
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.step = step;
        count.set(firstIndex);
    }

    public void lock(int iterNum) {

        if (!checkIteration(iterNum)) {
            throw new IllegalArgumentException("Iteration " + iterNum + " is out of the loop range " + firstIndex
                    + ".." + lastIndex);
        }

        if (conditions.containsKey(iterNum)) {
            throw new IllegalArgumentException("Condition for iteration " + iterNum + " already exists");
        }
        lock.lock();
        try {
            if (count.get() != iterNum) {
                Condition condition = lock.newCondition();
                conditions.put(iterNum, condition);
                LoggingUtils.debug(log, "OMP Thread " + OMPRuntime.getThreadNum()
                        + " awaiting ordered lock on iteration " + iterNum);
                condition.await();
            }
            LoggingUtils.debug(log, "OMP Thread " + OMPRuntime.getThreadNum() + " acquired ordered lock on iteration "
                    + iterNum);
        } catch (InterruptedException e) {
            throw new OMPRuntimeException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void unlock(int iterNum) {
        lock.lock();
        if (!count.compareAndSet(iterNum, getNextIteration(iterNum))) {
            throw new IllegalStateException("Attempt to unlock not the current iteration");
        }
        try {
            Condition condition = conditions.get(getNextIteration(iterNum));
            if (condition != null) {
                LoggingUtils.debug(log, "OMP Thread " + OMPRuntime.getThreadNum()
                        + " signal ordered lock on iteration " + iterNum);
                condition.signal();
            }
            LoggingUtils.debug(log, "OMP Thread " + OMPRuntime.getThreadNum() + " released ordered lock on iteration "
                    + iterNum);
        } finally {
            lock.unlock();
        }
    }

    protected abstract boolean checkIteration(int iterNum);

    protected abstract int getNextIteration(int iterNum);

    private static class DirectOrderedLock extends OrderedLock {

        public DirectOrderedLock(int firstIndex, int lastIndex, int step) {
            super(firstIndex, lastIndex, step);
        }

        @Override
        protected boolean checkIteration(int iterNum) {
            if (iterNum > lastIndex || iterNum < firstIndex) {
                return false;
            }
            return true;
        }

        @Override
        protected int getNextIteration(int iterNum) {
            return iterNum + step;
        }
    }

    private static class InverseOrderedLock extends OrderedLock {

        public InverseOrderedLock(int firstIndex, int lastIndex, int step) {
            super(firstIndex, lastIndex, step);
        }

        @Override
        protected boolean checkIteration(int iterNum) {
            if (iterNum < lastIndex || iterNum > firstIndex) {
                return false;
            }
            return true;
        }

        @Override
        protected int getNextIteration(int iterNum) {
            return iterNum - step;
        }
    }
}
