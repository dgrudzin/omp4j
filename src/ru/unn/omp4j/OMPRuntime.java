package ru.unn.omp4j;

import ru.unn.omp4j.thread.OMPThread;

/**
 * Contains all the runtime routines necessary to control execution of a parallel program.
 * 
 * @author Dmitry Grudzinskiy
 * 
 * @version 1.0
 *
 */
public class OMPRuntime {

    private static int defaultNumThreads = 2;
    private static int maxThreads;
    private static boolean nested = true;
    
    private static int maxTasks = 10;

    /**
     * Set a number of threads to be created in the all further parallel regions
     * 
     * @param numThreads - number of threads
     */
    public static void setNumThreads(int numThreads) {
        defaultNumThreads = numThreads;
    }

    /**
     * Returns the number of threads in current parallel region and/or in the all further regions
     * 
     * @return number of threads
     */
    public static int getNumThreads() {
        if (!inParallel()) {
            return defaultNumThreads;
        }
        return OMPEngine.getInstance().getActiveParallelRegion().getNumThreads();
    }

    /**
     * Returns the default number of threads
     *  
     * @return - number of threads
     */
    public static int getDefaultNumThreads() {
        return defaultNumThreads;
    }

    /**
     * Returns the maximum number of threads in any parallel regions
     * 
     */
    public static int getMaxThreads() {
        return maxThreads;
    }

    /**
     * Returns a number of a calling thread in current parallel region
     * or 0 if the thread is outside all the regions
     * 
     */
    public static int getThreadNum() {
        if (!inParallel()) {
            return 0;
        }
        return OMPEngine.getInstance().getOMPThread().getNum();
    }

    /**
     * Returns the number of processors available in the system
     * 
     */
    public static int getNumProcs() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Returns true if the calling thread is executing a parallel region 
     * 
     */
    public static boolean inParallel() {
        if (OMPEngine.getInstance().tryGetOmpThread() == null) {
            return false;
        }
        return true;
    }

    /**
     * Returns if nested parallelism is turned on 
     * 
     */
    public static boolean getNested() {
        return nested;
    }

    /**
     * Set nested parallelism
     * 
     */
    public static void setNested(boolean n) {
        nested = n;
    }

    /**
     * Returns the level of the current parallel region or 0
     * 
     */
    public static int getLevel() {
        OMPThread thread = OMPEngine.getInstance().tryGetOmpThread();
        if (thread == null) {
            return 0;
        }
        return thread.getActiveRegion().getLevelNumber();
    }

    /**
     * Set the maximum number of not executed tasks
     * 
     */
    public static void setMaxTasks(int maxT) {
        maxTasks = maxT;
    }

    /**
     * Returns the maximum number of not executed tasks
     * 
     */
    public static int getMaxTasks() {
        return maxTasks;
    }

    /**
     * Returns true if the calling thread is the master of the current parallel region
     * 
     */
    public boolean isMaster() {
        return OMPEngine.getInstance().getOMPThread().getNum() == 0;
    }

    private OMPRuntime() {
    }
}
