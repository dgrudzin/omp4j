package ru.unn.omp4j.thread;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.unn.omp4j.OMPEngine;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.OMPRuntimeException;
import ru.unn.omp4j.directives.OMPThreadBarrier;
import ru.unn.omp4j.directives.workshare.Chunk;
import ru.unn.omp4j.directives.workshare.ChunkProcessor;
import ru.unn.omp4j.directives.workshare.ChunkProcessorFactory;
import ru.unn.omp4j.directives.workshare.ForLoop;
import ru.unn.omp4j.directives.workshare.OrderedLock;
import ru.unn.omp4j.directives.workshare.Sections;
import ru.unn.omp4j.task.OMPTask;
import ru.unn.omp4j.task.ParallelTask;
import ru.unn.omp4j.task.TaskFactory;
import ru.unn.omp4j.thread.nat.NativeThreadPool;
import ru.unn.omp4j.thread.nat.OMPNativeThreadPool;
import ru.unn.omp4j.util.CollectionUtils;
import ru.unn.omp4j.util.LoggingUtils;

public class ParallelRegion {

    private static final Log log = LogFactory.getLog(ParallelRegion.class);

    private OMPThread[] team;

    private ParallelRegion parent;

    private int levelNumber = 1;

    private NativeThreadPool<OMPThread> pool;

    private final Lock taskLock = new ReentrantLock();

    private final Deque<OMPTask> tasks = new LinkedList<OMPTask>();

    private final Map<Integer, OMPThreadBarrier> barriers = CollectionUtils.newConcurrentHashMap();

    private CyclicBarrier finalBarrier;

    private final Lock barrierLock = new ReentrantLock();

    private final Map<Integer, ChunkProcessor> forLoops = CollectionUtils.newConcurrentHashMap();

    private final Lock forLock = new ReentrantLock();

    private final Set<Integer> singles = CollectionUtils.newHashSet();

    private final Lock singleLock = new ReentrantLock();

    private final Map<Integer, Sections> sectionsMap = CollectionUtils.newConcurrentHashMap();

    private final Lock sectionsLock = new ReentrantLock();

    private final Map<String, Lock> criticalLocks = CollectionUtils.newConcurrentHashMap();

    private final Lock crLock = new ReentrantLock();

    private boolean active = false;

    public ParallelRegion(int threadNum, NativeThreadPool<OMPThread> pool) {
        long start = System.currentTimeMillis();
        if (threadNum < 1) {
            throw new IllegalArgumentException("Thread number must be > 0");
        }
        this.pool = pool;
        team = new OMPThread[threadNum];
        finalBarrier = new CyclicBarrier(threadNum);

        //Check if already inside a parallel region
        OMPThread master = OMPEngine.getInstance().tryGetOmpThread();
        if (master != null) {
            parent = master.getActiveRegion();
            levelNumber = parent.getLevelNumber() + 1;
            team[0] = master;
            team[0].setLevel(new Level(master.getLevel(), 0, this));
        } else {
            team[0] = new OMPThread(new Level(0, this), pool);
        }

        for (int i = 1; i < threadNum; i++) {
            team[i] = new OMPThread(new Level(i, this), pool);
        }
        LoggingUtils.debug(log, "Initializing parallel region " + (System.currentTimeMillis() - start));
    }

    public ParallelRegion(int threadNum) {
        this(threadNum, new OMPNativeThreadPool<OMPThread>(threadNum));
    }

    public void runParallel(ParallelTask task) {

        long start = System.currentTimeMillis();
        
        if (active) {
            throw new IllegalStateException("Unable to run already active parallel region");
        }

        for (OMPThread t : team) {
            t.setRootTask(TaskFactory.newTiedTask(task));
        }

        active = true;

        for (int i = 1; i < team.length; i++) {
            pool.executeTask(team[i]);
        }

        LoggingUtils.debug(log, "Starting tasks " + (System.currentTimeMillis() - start));
        team[0].run();

        start = System.currentTimeMillis();
        if (!tasks.isEmpty()) {
            throw new OMPRuntimeException("Parallel region finished bu there are some tasks waiting to execute!");
        }

        active = false;
        
        barriers.clear();
        forLoops.clear();
        singles.clear();
        pool.kill();
        LoggingUtils.debug(log, "Killing region " + (System.currentTimeMillis() - start));
        if (levelNumber > 1) {
            team[0].setLevel(team[0].getLevel().getParent());
            OMPEngine.getInstance().putJavaOmpThreadRelation(team[0]);
        }
    }

    public void enterBarrier(int barrierNum, int threadNum) {

        if (!active) {
            throw new IllegalStateException("Parallel Region is not active, unable to enter/escape a barrier");
        }

        if (log.isDebugEnabled()) {
            log.debug("OMP thread " + threadNum + " is entering barrier " + barrierNum);
        }

        if (barrierNum < 0) {
            throw new IllegalArgumentException("Barrier number must be >= 0");
        }

        if (threadNum < 0 || threadNum > team.length - 1) {
            throw new IllegalArgumentException("Thread with number " + threadNum + " not registered in this region");
        }

        OMPThreadBarrier barrier;

        barrierLock.lock();
        try {
            barrier = barriers.get(barrierNum);
            if (barrier == null) {
                if (log.isDebugEnabled()) {
                    log.debug("OMP thread " + threadNum + " is the first to enter barrier " + barrierNum);
                }
                barrier = new OMPThreadBarrier(team.length);
                barriers.put(barrierNum, barrier);
            }
        } finally {
            barrierLock.unlock();
        }

        //this Java thread will wait
        barrier.await(team[threadNum]);
    }

    public boolean escapeBarrier(int barrierNum, int threadNum) {

        if (!active) {
            throw new IllegalStateException("Parallel Region is not active, unable to enter/escape a barrier");
        }

        if (barrierNum < 1) {
            throw new IllegalArgumentException("Barrier number must be > 0");
        }

        if (threadNum < 0 || threadNum > team.length - 1) {
            throw new IllegalArgumentException("Thread with number " + threadNum + " not registered in this region");
        }

        OMPThreadBarrier barrier = barriers.get(barrierNum);
        if (barrier == null) {
            throw new IllegalArgumentException("No barrier with id " + barrierNum + " registered");
        }

        return barrier.release(threadNum);
    }

    public void finalBarrier() {
        try {
            finalBarrier.await();
        } catch (InterruptedException e) {
            return;
        } catch (BrokenBarrierException e) {
            throw new OMPRuntimeException(e.getMessage());
        }
    }

    public void runForLoop(int loopId, ForLoop forLoop, ChunkProcessorFactory chFactory, int threadNum, boolean nowait) {
        if (!active) {
            throw new IllegalStateException("Unable to run a for loop in inactive state");
        }

        ChunkProcessor chProcessor = forLoops.get(loopId);
        if (chProcessor == null) {
            forLock.lock();
            try {
                chProcessor = forLoops.get(loopId);
                if (chProcessor == null) {
                    LoggingUtils.debug(log, "Adding for loop construct with id " + loopId);
                    long start = System.currentTimeMillis();
                    chProcessor = chFactory.createChunkProcessor();
                    log.debug("Creating chunk processor " + (System.currentTimeMillis() - start));
                    forLoops.put(loopId, chProcessor);
                }
            } finally {
                forLock.unlock();
            }
        }

        Chunk chunk;
        while ((chunk = chProcessor.getNextChunk(threadNum)) != null) {
            forLoop.execute(chunk);
        }
        if (!nowait) {
            team[threadNum].getActiveTask().barrier(team[threadNum].getLevel());
        }
        LoggingUtils.debug(log, "OMP thread " + threadNum + " finished for loop construct " + loopId);
    }

    public void runSingle(int singleId, int threadNum, ParallelTask task, boolean nowait) {
        if (!active) {
            throw new IllegalStateException("Unable to run a single construct in inactive state");
        }

        if (!singles.contains(singleId)) {

            singleLock.lock();
            try {
                if (!singles.contains(singleId)) {
                    singles.add(singleId);
                    LoggingUtils.debug(log, "OMP Thread " + threadNum + " executing single task " + singleId);
                    task.execute();
                }
            } finally {
                singleLock.unlock();
            }
        }
        if (!nowait) {
            team[threadNum].getActiveTask().barrier(team[threadNum].getLevel());
        }
    }

    public void runSections(int sectionsId, int threadNum, ParallelTask[] tasks, boolean nowait) {
        if (!active) {
            throw new IllegalStateException("Unable to run a sections construct in inactive state");
        }

        Sections sections = sectionsMap.get(sectionsId);
        if (sections == null) {
            sectionsLock.lock();
            try {
                sections = sectionsMap.get(sectionsId);
                if (sections == null) {
                    sections = new Sections(tasks);
                    sectionsMap.put(sectionsId, sections);
                }
            } finally {
                sectionsLock.unlock();
            }
        }
        ParallelTask toRun = sections.getNextTask();
        if (toRun != null) {
            LoggingUtils.debug(log, "OMP Thread " + threadNum + " executing section from sections " + sectionsId);
            toRun.execute();
        }
        if (!nowait) {
            team[threadNum].getActiveTask().barrier(team[threadNum].getLevel());
        }
    }

    public Lock getCriticalLock(String name) {
        if (!active) {
            throw new IllegalStateException("Unable to return a critical lock when inactive");
        }
        Lock lock = criticalLocks.get(name);
        if (lock == null) {
            crLock.lock();
            try {
                lock = criticalLocks.get(name);
                if (lock == null) {
                    lock = new ReentrantLock();
                    criticalLocks.put(name, lock);
                }
            } finally {
                crLock.unlock();
            }
        }
        return lock;
    }

    public OrderedLock getOrderedLock(int forLoopId) {
        if (!active) {
            throw new IllegalStateException("Unable to return an ordered lock when inactive");
        }
        ChunkProcessor chProcessor = forLoops.get(forLoopId);
        if (chProcessor == null) {
            throw new IllegalArgumentException("Unable to find loop with id " + forLoopId);
        }
        return chProcessor.getOrderedLock();
    }

    public void addTaskToHead(OMPTask task) {
        taskLock.lock();
        try {
            tasks.offerFirst(task);
        } finally {
            taskLock.unlock();
        }
    }

    public void addTaskToTail(OMPTask task) {
        taskLock.lock();
        try {
            tasks.offerLast(task);
        } finally {
            taskLock.unlock();
        }
    }

    public OMPTask getTask() {
        taskLock.lock();
        try {
            return tasks.pollFirst();
        } finally {
            taskLock.unlock();
        }
    }

    public boolean tasksOverHead() {
        return tasks.size() > OMPRuntime.getMaxTasks();
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public ParallelRegion getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isActive() {
        return active;
    }

    public int getNumThreads() {
        return team.length;
    }

    public OMPThread getThread(int threadNum) {
        if (threadNum < 0 || threadNum >= team.length) {
            throw new IllegalArgumentException("Invalid thread num " + threadNum);
        }
        return team[threadNum];
    }
}
