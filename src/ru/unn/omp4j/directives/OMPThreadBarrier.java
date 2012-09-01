package ru.unn.omp4j.directives;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.unn.omp4j.thread.OMPThread;
import ru.unn.omp4j.util.CollectionUtils;

public class OMPThreadBarrier {

    private static final Log log = LogFactory.getLog(OMPThreadBarrier.class);

    private int threadNum;

    private final Lock lock = new ReentrantLock();

    private final Map<Integer, Condition> conditionMap = CollectionUtils.newHashMap();

    private final AtomicInteger count = new AtomicInteger(0);

    public OMPThreadBarrier(int threadNum) {
        this.threadNum = threadNum;
    }

    public void await(OMPThread thread) {
        lock.lock();
        try {
            if (conditionMap.containsKey(thread.getNum())) {
                throw new IllegalArgumentException("Thread with id " + thread.getNum()
                        + " is already awaiting on this barrier");
            }
            if (count.getAndIncrement() == threadNum) {
                throw new IllegalStateException("Barrier already has " + threadNum + " waiting threads");
            }

            if (log.isDebugEnabled()) {
                log.debug("OMP thread " + thread.getNum() + " entered barrier, count = " + count.get());
            }

            Condition condition = lock.newCondition();
            conditionMap.put(thread.getNum(), condition);
            thread.goWithPool();
            condition.await();
        } catch (InterruptedException e) {
            return;
        } finally {
            lock.unlock();
        }
    }

    public boolean release(int threadId) {
        lock.lock();
        try {
            Condition condition = conditionMap.get(threadId);
            if (condition == null) {
                throw new IllegalArgumentException("Thread with id " + threadId + " is not awaiting on this barrier");
            }

            if (count.get() < threadNum) {
                if (log.isDebugEnabled()) {
                    log.debug("OMP thread " + threadId + " unnsuccessfully tried to escape from a barrier: count = "
                            + count.get() + ", threadNum " + threadNum);
                }
                return false;
            }

            conditionMap.remove(threadId);
            condition.signal();
            if (log.isDebugEnabled()) {
                log.debug("OMP thread " + threadId + " successfully escaped from a barrier");
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
}
