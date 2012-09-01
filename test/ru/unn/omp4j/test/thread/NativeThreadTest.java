package ru.unn.omp4j.test.thread;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;

import ru.unn.omp4j.thread.nat.OMPNativeThread;
import ru.unn.omp4j.thread.nat.OMPNativeThreadPool;

public class NativeThreadTest {

    @Test
    public void testSumValuesByThreads() {

        OMPNativeThread<TestNativeTask> thread = new OMPNativeThread<TestNativeTask>(1);

        AtomicInteger val = new AtomicInteger(0);
        TestNativeTask task = new TestNativeTask(val);

        thread.runTask(task);
        thread.joinTask();
        Assert.assertEquals("Incremented value", 1, val.get());

        thread.runTask(task);
        thread.joinTask();
        Assert.assertEquals("Incremented value", 2, val.get());

        thread.kill();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSimpleThreadPool() {
        AtomicInteger val = new AtomicInteger(0);
        TestNativeTask task = new TestNativeTask(val);

        OMPNativeThreadPool<TestNativeTask> pool = new OMPNativeThreadPool<TestNativeTask>(10);
        for (int i = 0; i < 10; i++) {
            pool.executeTask(task);
        }
        pool.join();
        pool.kill();
        Assert.assertEquals("Incremented value", 10, val.get());
    }

    @Test
    public void testMoreTasks() {
        AtomicInteger val = new AtomicInteger(0);
        TestNativeTask task = new TestNativeTask(val);

        OMPNativeThreadPool<TestNativeTask> pool = new OMPNativeThreadPool<TestNativeTask>(10);
        for (int i = 0; i < 15; i++) {
            pool.executeTask(task);
        }
        pool.join();
        pool.kill();
        Assert.assertEquals("Incremented value", 15, val.get());
    }
    
    private static class TestNativeTask implements Runnable {

        private volatile AtomicInteger value;

        private TestNativeTask(AtomicInteger i) {
            value = i;
        }

        @Override
        public void run() {
           // System.out.println("Thread " + Thread.currentThread().getName() + " value " + value.getAndIncrement());
            value.getAndIncrement();
        }
    }
}
