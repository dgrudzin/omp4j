package ru.unn.omp4j.thread.nat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OMPNativeThread<R extends Runnable> extends Thread implements NativeThread<R> {

    private int id;

    private R task;

    private Lock runLock = new ReentrantLock();

    private Condition runCondition = runLock.newCondition();

    private Condition activeCondition = runLock.newCondition();

    private boolean working = true;

    private Set<ThreadFinishedListener<R>> listeners = new HashSet<ThreadFinishedListener<R>>();

    public OMPNativeThread(int id) {
        this.id = id;
        super.start();
    }

    public void runTask(R r) {
        if (!working) {
            throw new IllegalStateException("Unable to run task by finished thread");
        }
        runLock.lock();
        try {
            this.task = r;
            runCondition.signal();
        } finally {
            runLock.unlock();
        }
    }

    public void run() {
        while (working) {
            runLock.lock();
            try {
                while (task == null) {
                    runCondition.await();
                }

                //Run task
                task.run();
                task = null;

                //Signal to all the threads that wait 
                //the task to complete
                activeCondition.signalAll();

                //Notify all the listeners that thread is free
                notifyStateChanged();
            } catch (InterruptedException e) {
                return;
            } finally {
                runLock.unlock();
            }
        }
    }

    public void joinTask() {
        runLock.lock();
        try {
            while (task != null) {
                activeCondition.await();
            }
        } catch (InterruptedException e) {
            return;
        } finally {
            runLock.unlock();
        }
    }

    public boolean isActive() {
        return task != null;
    }

    public R getRunnable() {
        return task;
    }

    public void kill() {
        runLock.lock();
        try {
            working = false;
            interrupt();
        } finally {
            runLock.unlock();
        }
    }

    public void addListener(ThreadFinishedListener<R> l) {
        listeners.add(l);
    }

    public boolean removeListener(ThreadFinishedListener<R> l) {
        return listeners.remove(l);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OMPNativeThread other = (OMPNativeThread) obj;
        if (id != other.id)
            return false;
        return true;
    }

    private void notifyStateChanged() {
        for (ThreadFinishedListener<R> l : listeners) {
            l.threadFree(this);
        }
    }
}
