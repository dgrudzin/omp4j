package ru.unn.omp4j;

import java.util.concurrent.locks.Lock;

import ru.unn.omp4j.directives.workshare.DynamicChunkProcessor;
import ru.unn.omp4j.directives.workshare.ForLoop;
import ru.unn.omp4j.directives.workshare.OrderedLock;
import ru.unn.omp4j.directives.workshare.StaticChunkProcessor;
import ru.unn.omp4j.task.OMPTask;
import ru.unn.omp4j.task.ParallelTask;
import ru.unn.omp4j.task.TaskFactory;
import ru.unn.omp4j.thread.Level;
import ru.unn.omp4j.thread.OMPThread;
import ru.unn.omp4j.thread.ParallelRegion;

/**
 * The class contains static methods that are analogs to corresponding OpenMP directives.
 * 
 * @author Dmitry Grudzinskiy
 *
 * @version 1.0
 */
public final class OMPDirectives {

    /**
     * Creates and starts a parallel region.
     * 
     * @param numThreads - number of threads in the team executing the region
     * @param task - task to be executed by each thread
     */
    public static void parallel(int numThreads, ParallelTask task) {
        ParallelRegion region = new ParallelRegion(numThreads);
        region.runParallel(task);
    }
    
    /**
     * Creates and starts a parallel region with number of threads in the team
     * specified by <code>ompGetNumThreads</code> runtime routine.
     * 
     * @param task - task to be executed by each thread
     */
    
    public static void parallel(ParallelTask task) {
        ParallelRegion region = new ParallelRegion(OMPRuntime.getDefaultNumThreads());
        region.runParallel(task);
    }

    /**
     * Creates an explicit task.
     * 
     * @param task - task to create
     * @param tied - determines if the generated task is tied
     */
    public static void task(ParallelTask task, boolean tied) {
        OMPTask ompTask;
        if (tied) {
            ompTask = TaskFactory.newTiedTask(task);
        } else {
            ompTask = TaskFactory.newUntiedTask(task);
        }

        OMPEngine.getInstance().getOMPThread().addTask(ompTask);
    }

    /**
     * Shares execution of a given for loop among threads of the team.
     * The iterations are distributed between threads statically.
     * 
     * @param loop - for loop to distribute
     * @param firstIndex - index of the first iteration
     * @param lastIndex - index of the last iteration
     * @param step - loop step
     * @param chunkSize - size of the chunks of work unit
     * @param nowait - if true there is no implicit barrier at the end of the construct
     */
    public static void staticFor(ForLoop loop, int firstIndex, int lastIndex, int step, int chunkSize, boolean nowait) {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        ParallelRegion region = level.getRegion();
        region.runForLoop(level.getNextForLoop(), loop, StaticChunkProcessor.getFactory(region.getNumThreads(),
                firstIndex, lastIndex, step, chunkSize), level.getThreadNum(), nowait);
    }

    /**
     * Shares execution of a given for loop among threads of the team.
     * The iterations are distributed between threads statically.
     * Chunk size is calculated in the way that each thread will execute only one chunk.
     * 
     * @param loop - for loop to distribute
     * @param firstIndex - index of the first iteration
     * @param lastIndex - index of the last iteration
     * @param step - loop step
     * @param nowait - if true there is no implicit barrier at the end of the construct
     */
    public static void staticFor(ForLoop loop, int firstIndex, int lastIndex, int step, boolean nowait) {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        ParallelRegion region = level.getRegion();
        region.runForLoop(level.getNextForLoop(), loop, StaticChunkProcessor.getFactory(region.getNumThreads(),
                firstIndex, lastIndex, step), level.getThreadNum(), nowait);
    }

    /**
     * Shares execution of a given for loop among threads of the team.
     * The iterations are distributed between threads dynamically.
     * 
     * @param loop - for loop to distribute
     * @param firstIndex - index of the first iteration
     * @param lastIndex - index of the last iteration
     * @param step - loop step
     * @param chunkSize - size of the chunks of work unit
     * @param nowait - if true there is no implicit barrier at the end of the construct
     */
    public static void dynamicFor(ForLoop loop, int firstIndex, int lastIndex, int step, int chunkSize, boolean nowait) {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        ParallelRegion region = level.getRegion();
        region.runForLoop(level.getNextForLoop(), loop, DynamicChunkProcessor.getFactory(region.getNumThreads(),
                firstIndex, lastIndex, step, chunkSize), level.getThreadNum(), nowait);
    }

    /**
     * Shares execution of a given for loop among threads of the team with default chunk size 1.
     * The iterations are distributed between threads dynamically.
     * 
     * @param loop - for loop to distribute
     * @param firstIndex - index of the first iteration
     * @param lastIndex - index of the last iteration
     * @param step - loop step
     * @param chunkSize - size of the chunks of work unit
     * @param nowait - if true there is no implicit barrier at the end of the construct
     */
    public static void dynamicFor(ForLoop loop, int firstIndex, int lastIndex, int step, boolean nowait) {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        ParallelRegion region = level.getRegion();
        region.runForLoop(level.getNextForLoop(), loop, DynamicChunkProcessor.getFactory(region.getNumThreads(),
                firstIndex, lastIndex, step), level.getThreadNum(), nowait);
    }

    /**
     * Creates a task and executes it only by one thread of the team.
     * 
     * @param task - task to execute 
     * @param nowait - if true there is no implicit barrier at the end of the construct
     */
    public static void singleTask(ParallelTask task, boolean nowait) {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        ParallelRegion region = level.getRegion();
        region.runSingle(level.getNextSingle(), level.getThreadNum(), task, nowait);
    }

    /**
     * Distributes specified tasks among threads of the team. 
     * Each task is executed only once by one thread. 
     * 
     * @param tasks - tasks to execute
     * @param nowait - if true there is no implicit barrier at the end of the construct
     */
    public static void sections(ParallelTask[] tasks, boolean nowait) {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        ParallelRegion region = level.getRegion();
        region.runSections(level.getNextSections(), level.getThreadNum(), tasks, nowait);
    }

    /**
     * Specifies an explicit barrier.
     * 
     */
    public static void barrier() {
        OMPThread thread = OMPEngine.getInstance().getOMPThread();
        thread.getActiveTask().barrier(thread.getLevel());
    }

    /**
     * Specifies a taskwait construct.
     * 
     */
    public static void taskwait() {
        OMPThread thread = OMPEngine.getInstance().getOMPThread();
        thread.getActiveTask().taskwait(thread.getLevel());
    }

    /**
     * Retrieves a critical lock for a specified name.
     * This lock may be then used to synchronize critical sections. 
     * 
     * @param name - name of the block
     * @return - critical lock by for the specified name
     */
    public static Lock getCriticalLock(String name) {
        return OMPEngine.getInstance().getActiveParallelRegion().getCriticalLock(name);
    }

    /**
     * Retrieves a critical lock for a 'default' name.
     * This lock may be then used to synchronize critical sections. 
     * 
     * @return - critical lock by for the name 'default'
     */
    public static Lock getCriticalLock() {
        return getCriticalLock("default");
    }

    /**
     * Returns an ordered lock that may be then used to create ordered sections of a for loop
     * 
     * @return - ordered lock
     */
    public static OrderedLock getOrderedLock() {
        Level level = OMPEngine.getInstance().getOMPThread().getLevel();
        return level.getRegion().getOrderedLock(level.getCurForLoop());
    }

    private OMPDirectives() {
    }
}
