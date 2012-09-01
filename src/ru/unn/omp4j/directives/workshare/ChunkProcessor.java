package ru.unn.omp4j.directives.workshare;

public abstract class ChunkProcessor {

    protected int numThreads;
    protected int firstIndex;
    protected int lastIndex;
    protected int step;
    protected int chunkSize;

    private OrderedLock orderedLock;

    public ChunkProcessor(int numThreads, int firstIndex, int lastIndex, int step, int chunkSize) {
        this.numThreads = numThreads;
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.step = step;
        this.chunkSize = chunkSize;
        this.orderedLock = OrderedLock.getInstance(firstIndex, lastIndex, step);
    }

    public OrderedLock getOrderedLock() {
        return orderedLock;
    }

    public abstract Chunk getNextChunk(int threadNum);
}
