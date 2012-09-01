package ru.unn.omp4j.thread.nat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorsNativePool<R extends Runnable> implements NativeThreadPool<R> {

    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void executeTask(R task) {
        executor.execute(task);
    }

    @Override
    public void join() {
    }

    @Override
    public void kill() {
        executor.shutdownNow();
    }
}
