package ru.unn.omp4j.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.unn.omp4j.OMPEngine;
import ru.unn.omp4j.task.OMPTask;
import ru.unn.omp4j.task.TiedTask;
import ru.unn.omp4j.thread.nat.NativeThreadPool;
import ru.unn.omp4j.util.LoggingUtils;

public class OMPThread implements Runnable {

    private static final Log log = LogFactory.getLog(OMPThread.class);

    private NativeThreadPool<OMPThread> pool;

    private Level level;

    private TiedTask rootTask;

    public OMPThread(Level level, NativeThreadPool<OMPThread> pool) {
        this.level = level;
        this.pool = pool;
    }

    public void addTask(OMPTask task) {
        if (level.getActiveTask() == null) {
            throw new IllegalStateException("OMP Thread " + level.getThreadNum()
                    + " doesn't have an active task, unable to add a child task");
        }
        LoggingUtils.debug(log, level, "adding a task");
        task.setParent(level.getActiveTask());
        level.getRegion().addTaskToHead(task);
        level.getActiveTask().schedulingPoint(level);
    }

    public void goWithPool() {
        pool.executeTask(this);
    }

    @Override
    public void run() {
        OMPEngine.getInstance().putJavaOmpThreadRelation(this);
        if (log.isDebugEnabled()) {
            log.debug("OMP thread " + getNum() + " running on native thread " + Thread.currentThread().getName());
        }
        if (rootTask != null) {
            LoggingUtils.debug(log, level, "executing root task");
            level.setActiveTask(rootTask);
            rootTask = null;

            level.getActiveTask().perform(level);

            level.setActiveTask(null);
        } else {

            while (true) {
                level.setActiveTask(level.getRegion().getTask());

                if (level.getActiveTask() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("OMP thread " + getNum() + " running next task on native thread "
                                + Thread.currentThread().getName());
                    }
                    if (!level.getActiveTask().perform(level)) {
                        OMPEngine.getInstance().removeJavaOmpThreadRelation();
                        return;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("OMP thread " + getNum() + " running next tied task on native thread "
                                + Thread.currentThread().getName());
                    }
                    OMPTask tiedTask = level.getNextTiedTask();
                    if (tiedTask != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("OMP thread " + getNum() + " running next tied task on native thread "
                                    + Thread.currentThread().getName() + " " +  tiedTask.toString());
                        }
                    }
                    level.setActiveTask(tiedTask);
                    if (!level.getActiveTask().perform(level)) {
                        OMPEngine.getInstance().removeJavaOmpThreadRelation();
                        return;
                    } else {
                        Thread.yield();
                    }
                }
                level.setActiveTask(null);
            }
        }

        if (log.isDebugEnabled()) {
            log
                    .debug("OMP thread " + getNum() + " finishing work on native thread "
                            + Thread.currentThread().getName());
        }
        level.finalBarrier();
        OMPEngine.getInstance().removeJavaOmpThreadRelation();
    }

    public int getNum() {
        return level.getThreadNum();
    }

    public ParallelRegion getActiveRegion() {
        return level.getRegion();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public OMPTask getActiveTask() {
        return level.getActiveTask();
    }

    public void setRootTask(TiedTask task) {
        this.rootTask = task;
    }

    public OMPThread copy() {
        return new OMPThread(level, pool);
    }
}
