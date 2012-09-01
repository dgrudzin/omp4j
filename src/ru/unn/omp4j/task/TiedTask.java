package ru.unn.omp4j.task;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.unn.omp4j.thread.Level;
import ru.unn.omp4j.thread.OMPThread;
import ru.unn.omp4j.util.LoggingUtils;

public class TiedTask extends OMPTask {

    protected static final Log log = LogFactory.getLog(TiedTask.class);

    private Lock sclock = new ReentrantLock();
    private Condition condition = sclock.newCondition();

    public TiedTask(ParallelTask managed, OMPTask parent) {
        super(managed, parent);
    }

    @Override
    public void barrier(Level level) {
        setState(OMPTaskState.STATE_IN_BARRIER);
        level.addTiedTaskToHead(this);
        level.enterBarrier();
    }

    @Override
    boolean runFromBarrier(Level level) {
        if (getChildrenSize() != 0) {
            level.addTiedTaskToTail(this);
            return false;
        }
        boolean success = level.escapeBarrier();

        if (success) {
            setState(OMPTaskState.STATE_IN_PROGRESS);
        } else {
            level.addTiedTaskToTail(this);
        }

        return success;
    }

    @Override
    public void schedulingPoint(Level level) {
        LoggingUtils.debug(log, level, " trying to enter a scheduling point of tied task");
        if (!level.getRegion().tasksOverHead()) {
            return;
        }
        waitInSchedulingPoint(level, OMPTaskState.STATE_SCHEDULING_POINT);
    }

    @Override
    boolean runFromSchedulingPoint(Level level) {
        LoggingUtils.debug(log, level, " trying to escape from a scheduling point of tied task");
        if (level.getRegion().tasksOverHead()) {
            level.addTiedTaskToTail(this);
            return true;
        }
        signalSchedulingPoint();
        return false;
    }

    @Override
    boolean runFromTaskWait(Level level) {
        LoggingUtils.debug(log, level, " trying to escape from a taskwait of tied task");
        if (getChildrenSize() != 0) {
            level.addTiedTaskToTail(this);
            return true;
        }
        signalSchedulingPoint();
        return false;
    }

    @Override
    public void taskwait(Level level) {
        LoggingUtils.debug(log, level, " trying to enter a taskwait of tied task");
        if (getChildrenSize() == 0) {
            return;
        }
        waitInSchedulingPoint(level, OMPTaskState.STATE_TASKWAIT);
    }

    private void waitInSchedulingPoint(Level level, OMPTaskState newState) {
        setState(newState);
        level.addTiedTaskToHead(this);
        OMPThread curThread = level.getRegion().getThread(level.getThreadNum());

        sclock.lock();
        try {
            curThread.goWithPool();
            condition.await();
        } catch (InterruptedException e) {
            return;
        } finally {
            sclock.unlock();
        }
    }

    private void signalSchedulingPoint() {
        sclock.lock();
        try {
            setState(OMPTaskState.STATE_IN_PROGRESS);
            condition.signal();
        } finally {
            sclock.unlock();
        }
    }
}
