package ru.unn.omp4j.thread;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ru.unn.omp4j.task.OMPTask;
import ru.unn.omp4j.task.TiedTask;

public class Level {

    private Level parent;

    private AtomicReference<OMPTask> activeTask = new AtomicReference<OMPTask>();

    private final AtomicInteger barrierCounter = new AtomicInteger(1);

    private final AtomicInteger forLoopConuter = new AtomicInteger(1);

    private final AtomicInteger singleCounter = new AtomicInteger(1);

    private final AtomicInteger sectionsCounter = new AtomicInteger(1);

    private int threadNum;

    private ParallelRegion region;

    private Deque<TiedTask> tiedTasks = new LinkedList<TiedTask>();

    public Level(Level parent, int threadNum, ParallelRegion region) {
        this.parent = parent;
        this.threadNum = threadNum;
        this.region = region;
    }

    public Level(int threadNum, ParallelRegion region) {
        this(null, threadNum, region);
    }

    public OMPTask getNextTiedTask() {
        return tiedTasks.pollFirst();
    }

    public void addTiedTaskToHead(TiedTask task) {
        tiedTasks.offerFirst(task);
    }

    public void addTiedTaskToTail(TiedTask task) {
        tiedTasks.offerLast(task);
    }

    public void enterBarrier() {
        region.enterBarrier(barrierCounter.getAndIncrement(), threadNum);
    }

    public boolean escapeBarrier() {
        return region.escapeBarrier(barrierCounter.get() - 1, threadNum);
    }

    public int getNextForLoop() {
        return forLoopConuter.getAndIncrement();
    }

    public int getCurForLoop() {
        return forLoopConuter.get() - 1;
    }

    public int getNextSingle() {
        return singleCounter.getAndIncrement();
    }

    public int getNextSections() {
        return sectionsCounter.getAndIncrement();
    }

    public Level getParent() {
        return parent;
    }

    public ParallelRegion getRegion() {
        return region;
    }

    public void finalBarrier() {
        region.finalBarrier();
    }

    public int getThreadNum() {
        return threadNum;
    }

    public OMPTask getActiveTask() {
        return activeTask.get();
    }

    public void setActiveTask(OMPTask activeTask) {
        this.activeTask.set(activeTask);
    }
}
