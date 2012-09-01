package ru.unn.omp4j.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.unn.omp4j.thread.Level;

public abstract class OMPTask implements ParallelTask {

    private OMPTask parent;

    private Set<OMPTask> children;

    private Lock lock = new ReentrantLock();

    private ParallelTask managed;

    private OMPTaskState state;

    public OMPTask(ParallelTask managed, OMPTask parent) {
        this.managed = managed;
        this.parent = parent;
        children = new HashSet<OMPTask>();
        setState(OMPTaskState.STATE_READY);
    }

    @Override
    public void execute() {
        if (managed != null) {
            setState(OMPTaskState.STATE_IN_PROGRESS);
            managed.execute();
            if (parent != null) {
                parent.removeChild(this);
            }
            setState(OMPTaskState.STATE_COMPLETED);
        }
    }

    public boolean perform(Level level) {
        return state.perform(this, level);
    }

    public OMPTask getParent() {
        return parent;
    }

    public void setParent(OMPTask parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public Set<OMPTask> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public void addChild(OMPTask child) {
        lock.lock();
        try {
            children.add(child);
        } finally {
            lock.unlock();
        }
    }

    public void removeChild(OMPTask child) {
        lock.lock();
        try {
            children.remove(child);
        } finally {
            lock.unlock();
        }
    }

    public int getChildrenSize() {
        return children.size();
    }

    @Override
    public String toString() {
        return "Task in State " + state.toString();
    }

    public abstract void barrier(Level level);

    public abstract void schedulingPoint(Level level);

    public abstract void taskwait(Level level);

    abstract boolean runFromBarrier(Level level);

    abstract boolean runFromSchedulingPoint(Level level);

    abstract boolean runFromTaskWait(Level level);

    protected void setState(OMPTaskState state) {
        this.state = state;
    }
}
