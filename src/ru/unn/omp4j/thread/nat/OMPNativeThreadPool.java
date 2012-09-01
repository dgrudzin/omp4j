package ru.unn.omp4j.thread.nat;

import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OMPNativeThreadPool<R extends Runnable> implements NativeThreadPool<R>, ThreadFinishedListener<R> {

    private Queue<OMPNativeThread<R>> freeThreads = new ConcurrentLinkedQueue<OMPNativeThread<R>>();

    private HashSet<OMPNativeThread<R>> allThreads = new HashSet<OMPNativeThread<R>>();

    private AtomicInteger threadId = new AtomicInteger(0);

    private Lock lock = new ReentrantLock();

    public OMPNativeThreadPool(int initialSize) {
        for (int i = 0; i < initialSize; i++) {
            OMPNativeThread<R> nt = createThread();

            allThreads.add(nt);
            freeThreads.add(nt);
            
            nt.addListener(this);
        }
    }

    public void executeTask(R task) {
        lock.lock();
        try {
            OMPNativeThread<R> nt;
            if (!freeThreads.isEmpty()) {
                nt = freeThreads.poll();
            } else {
                nt = createThread();
                allThreads.add(nt);
            }
            nt.runTask(task);
        } finally {
            lock.unlock();
        }
    }

    public int getPoolSize() {
        return allThreads.size();
    }
    
    public void kill() {
        for (OMPNativeThread<R> nt : allThreads) {
            nt.kill();
        }

        for (OMPNativeThread<R> nt : allThreads) {
            try {
                nt.join();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public void threadFree(OMPNativeThread<R> nt) {
        freeThreads.add(nt);
    }

    private OMPNativeThread<R> createThread() {
        int id = threadId.getAndIncrement();
        OMPNativeThread<R> nt = new OMPNativeThread<R>(id);
        nt.setName("NativeThread_" + id);
        if (nt.getPriority() != Thread.NORM_PRIORITY) {
            nt.setPriority(Thread.NORM_PRIORITY);
        }
        return nt;
    }

    @Override
    public void join() {
        for (OMPNativeThread<R> nt : allThreads) {
            nt.joinTask();
        }
    }
}
