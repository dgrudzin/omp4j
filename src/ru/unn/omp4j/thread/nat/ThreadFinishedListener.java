package ru.unn.omp4j.thread.nat;

public interface ThreadFinishedListener<R extends Runnable> {

    void threadFree(OMPNativeThread<R> nt);
}
