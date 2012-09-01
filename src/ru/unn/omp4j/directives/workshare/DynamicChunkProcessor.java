package ru.unn.omp4j.directives.workshare;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DynamicChunkProcessor extends ChunkProcessor {

    private Queue<Chunk> chunks = new ConcurrentLinkedQueue<Chunk>();

    public static ChunkProcessorFactory getFactory(final int numThreads, final int firstIndex, final int lastIndex,
            final int step, final int chunkSize) {
        return new ChunkProcessorFactory() {

            @Override
            public ChunkProcessor createChunkProcessor() {
                return new DynamicChunkProcessor(numThreads, firstIndex, lastIndex, step, chunkSize);
            }
        };
    }

    public static ChunkProcessorFactory getFactory(final int numThreads, final int firstIndex, final int lastIndex,
            final int step) {
        return new ChunkProcessorFactory() {

            @Override
            public ChunkProcessor createChunkProcessor() {
                return new DynamicChunkProcessor(numThreads, firstIndex, lastIndex, step);
            }
        };
    }

    public DynamicChunkProcessor(int numThreads, int firstIndex, int lastIndex, int step, int chunkSize) {
        super(numThreads, firstIndex, lastIndex, step, chunkSize);
        fillChunks();
    }

    public DynamicChunkProcessor(int numThreads, int firstIndex, int lastIndex, int step) {
        this(numThreads, firstIndex, lastIndex, step, 1);
    }

    @Override
    public Chunk getNextChunk(int threadNum) {
        return chunks.poll();
    }

    private void fillChunks() {
        int allSize = Math.abs(lastIndex - firstIndex) + 1;
        int taskNumber = allSize % chunkSize == 0 ? allSize / chunkSize : allSize / chunkSize + 1;

        int st = firstIndex;
        for (int i = 0; i < taskNumber; i++) {
            if (lastIndex > firstIndex) {
                int fin = Math.min(st + chunkSize - 1, lastIndex);
                chunks.offer(new Chunk(st, fin, step));
                st = fin + 1;
            } else {
                int fin = Math.max(st - chunkSize + 1, lastIndex);
                chunks.offer(new Chunk(st, fin, step));
                st = fin - 1;
            }
        }
    }
}
