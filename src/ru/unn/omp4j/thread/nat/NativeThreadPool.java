package ru.unn.omp4j.thread.nat;

public interface NativeThreadPool<R extends Runnable> {

    void executeTask(R task);
    
    void kill();
    
    void join();
}
