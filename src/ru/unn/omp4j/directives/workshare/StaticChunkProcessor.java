package ru.unn.omp4j.directives.workshare;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import ru.unn.omp4j.util.CollectionUtils;

public class StaticChunkProcessor extends ChunkProcessor {

    private Map<Integer, Queue<Chunk>> chunks = CollectionUtils.newHashMap();

    public static ChunkProcessorFactory getFactory(final int numThreads, final int firstIndex, final int lastIndex, final int step,
            final int chunkSize) {
        return new ChunkProcessorFactory() {

            @Override
            public ChunkProcessor createChunkProcessor() {
                return new StaticChunkProcessor(numThreads, firstIndex, lastIndex, step, chunkSize);
            }
        };
    }

    public static ChunkProcessorFactory getFactory(final int numThreads, final int firstIndex, final int lastIndex, final int step) {
        return new ChunkProcessorFactory() {

            @Override
            public ChunkProcessor createChunkProcessor() {
                return new StaticChunkProcessor(numThreads, firstIndex, lastIndex, step);
            }
        };
    }

    public StaticChunkProcessor(int numThreads, int firstIndex, int lastIndex, int step, int chunkSize) {
        super(numThreads, firstIndex, lastIndex, step, chunkSize);
        fillChunks();
    }

    public StaticChunkProcessor(int numThreads, int firstIndex, int lastIndex, int step) {
        this(numThreads, firstIndex, lastIndex, step, 0);
    }

    @Override
    public final Chunk getNextChunk(int threadNum) {
        Queue<Chunk> threadQueue = chunks.get(threadNum);
        if (threadQueue == null) {
            return null;
        }
        return threadQueue.poll();
    }

    private void fillChunks() {
        int allSize = Math.abs(lastIndex - firstIndex) + 1;
        int taskNumber;
        if (chunkSize == 0) {
            taskNumber = Math.min(numThreads, allSize);
            chunkSize = allSize % taskNumber == 0 ? allSize / taskNumber : allSize / taskNumber + 1;
        } else {
            taskNumber = allSize % chunkSize == 0 ? allSize / chunkSize : allSize / chunkSize + 1;
        }

        int st = firstIndex;
        for (int i = 0; i < taskNumber; i++) {
            int threadNum = i % numThreads;
            Queue<Chunk> threadQueue = chunks.get(threadNum);
            if (threadQueue == null) {
                threadQueue = new LinkedList<Chunk>();
                chunks.put(threadNum, threadQueue);
            }
            if (lastIndex > firstIndex) {
                int fin = Math.min(st + chunkSize - 1, lastIndex);
                threadQueue.offer(new Chunk(st, fin, step));
                st = fin + 1;
            } else {
                int fin = Math.max(st - chunkSize + 1, lastIndex);
                threadQueue.offer(new Chunk(st, fin, step));
                st = fin - 1;
            }
        }
    }
}
