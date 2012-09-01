package ru.unn.omp4j.test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.task.ParallelTask;

public class CriticalTest {

    @Before
    public void setUp() {
        DOMConfigurator.configure("./conf/omp-log4j.xml");
    }

    @Test
    public void testCritical() {
        final AtomicInteger i = new AtomicInteger(0);
        OMPDirectives.parallel(10, new ParallelTask() {

            @Override
            public void execute() {
                Lock lock = OMPDirectives.getCriticalLock();
                lock.lock();
                try {
                    i.set(i.get() + 1);
                } finally {
                    lock.unlock();
                }
            }
        });
        Assert.assertEquals(10, i.get());
    }
}
