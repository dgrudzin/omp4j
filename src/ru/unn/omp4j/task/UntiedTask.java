package ru.unn.omp4j.task;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.unn.omp4j.thread.Level;
import ru.unn.omp4j.thread.OMPThread;
import ru.unn.omp4j.util.LoggingUtils;

public class UntiedTask extends OMPTask {

    private static final Log log = LogFactory.getLog(UntiedTask.class);

    private Lock sclock = new ReentrantLock();
    private Condition condition = sclock.newCondition();
    private OMPThread curThread = null;

    public UntiedTask(ParallelTask managed, OMPTask parent) {
        super(managed, parent);
    }

    @Override
    public void barrier(Level level) {
        throw new UnsupportedOperationException("Barrier not allowed for untied tasks");
    }

    @Override
    boolean runFromBarrier(Level level) {
        throw new UnsupportedOperationException("Barrier not allowed for untied tasks");
    }

    @Override
    public void schedulingPoint(Level level) {
        LoggingUtils.debug(log, level, " trying to enter a scheduling point of untied task");
        if (!level.getRegion().tasksOverHead()) {
            return;
        }

    }

    @Override
    boolean runFromSchedulingPoint(Level level) {
        LoggingUtils.debug(log, level, " trying to escape from a scheduling point of untied task");
        if (level.getRegion().tasksOverHead()) {
            level.getRegion().addTaskToTail(this);
            return true;
        }
        signalSchedulingPoint(level);
        return false;
    }

    @Override
    boolean runFromTaskWait(Level level) {
        LoggingUtils.debug(log, level, " trying to escape from a taskwait of untied task");
        if (getChildrenSize() != 0) {
            level.getRegion().addTaskToTail(this);
            return true;
        }
        signalSchedulingPoint(level);
        return false;
    }

    @Override
    public void taskwait(Level level) {
        LoggingUtils.debug(log, level, " trying to enter a taskwait of untied task");
        if (getChildrenSize() == 0) {
            return;
        }
        waitInSchedulingPoint(level, OMPTaskState.STATE_TASKWAIT);
    }

    private void waitInSchedulingPoint(Level level, OMPTaskState newState) {
        sclock.lock();
        try {
            if (curThread != null) {
                throw new IllegalStateException("The task is already entered this scheduling point by thread "
                        + curThread.getNum());
            }
            setState(OMPTaskState.STATE_SCHEDULING_POINT);
            level.getRegion().addTaskToHead(this);
            curThread = level.getRegion().getThread(level.getThreadNum());
            curThread.copy().goWithPool();
            curThread.setLevel(null);
            condition.await();
        } catch (InterruptedException e) {
            return;
        } finally {
            sclock.unlock();
        }
    }

    private void signalSchedulingPoint(Level level) {
        sclock.lock();
        try {
            setState(OMPTaskState.STATE_IN_PROGRESS);
            if (curThread == null) {
                throw new IllegalStateException("The task is not in scheduling point");
            }
            curThread.setLevel(level);
            condition.signal();
            curThread = null;
        } finally {
            sclock.unlock();
        }
    }
}
