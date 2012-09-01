package ru.unn.omp4j.thread.nat;

public interface NativeThread<R extends Runnable> {

    void runTask(R task);
    
    void joinTask();
    
    R getRunnable();
}
