package ru.unn.omp4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import ru.unn.omp4j.task.OMPTask;
import ru.unn.omp4j.thread.OMPThread;
import ru.unn.omp4j.thread.ParallelRegion;

public class OMPEngine {

    private static final AtomicReference<OMPEngine> ref = new AtomicReference<OMPEngine>();

    public static OMPEngine getInstance() {
        if (ref.get() == null) {
            ref.compareAndSet(null, new OMPEngine());
        }
        return ref.get();
    }

    private Map<Long, OMPThread> javaOMPThreadsMap;

    private OMPEngine() {
        javaOMPThreadsMap = Collections.synchronizedMap(new HashMap<Long, OMPThread>());
    }

    public void putJavaOmpThreadRelation(OMPThread thread) {
        javaOMPThreadsMap.put(Thread.currentThread().getId(), thread);
    }

    public void removeJavaOmpThreadRelation() {
        javaOMPThreadsMap.remove(Thread.currentThread().getId());
    }

    public OMPThread tryGetOmpThread() {
        return javaOMPThreadsMap.get(Thread.currentThread().getId());
    }

    public OMPThread getOMPThread() {
        OMPThread thread = tryGetOmpThread();
        if (thread == null) {
            throw new OMPRuntimeException("Thread is not registered in OMPEngine");
        }
        return thread;
    }

    public OMPTask getActiveTask() {
        OMPThread thread = getOMPThread();
        OMPTask activeTask = getOMPThread().getActiveTask();
        if (activeTask == null) {
            throw new OMPRuntimeException("OMP thread " + thread.getNum() + " doesn't have an active task");
        }
        return activeTask;
    }
    
    public ParallelRegion getActiveParallelRegion() {
        return getOMPThread().getActiveRegion();
    }
}
