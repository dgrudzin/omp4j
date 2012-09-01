package ru.unn.omp4j.test;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.directives.workshare.Chunk;
import ru.unn.omp4j.directives.workshare.ChunkProcessor;
import ru.unn.omp4j.directives.workshare.ForLoop;
import ru.unn.omp4j.directives.workshare.OrderedLock;
import ru.unn.omp4j.directives.workshare.StaticChunkProcessor;
import ru.unn.omp4j.task.ParallelTask;

public class ForLoopTest {

    @Before
    public void setUp() {
        DOMConfigurator.configure("./conf/omp-log4j.xml");
    }

    @Test
    public void testStaticForLoopChunk() {
        ChunkProcessor staticForLoop = new StaticChunkProcessor(10, 0, 40, 1, 2);

        Chunk chunk = staticForLoop.getNextChunk(0);
        Assert.assertEquals(new Chunk(0, 1, 1), chunk);

        chunk = staticForLoop.getNextChunk(0);
        Assert.assertEquals(new Chunk(20, 21, 1), chunk);
        chunk = staticForLoop.getNextChunk(0);
        Assert.assertEquals(new Chunk(40, 40, 1), chunk);
    }

    @Test
    public void testStaticInverseForLoopChunk() {
        ChunkProcessor staticForLoop = new StaticChunkProcessor(10, 40, 0, 1, 2);

        Chunk chunk = staticForLoop.getNextChunk(0);
        Assert.assertEquals(new Chunk(40, 39, 1), chunk);

        chunk = staticForLoop.getNextChunk(0);
        Assert.assertEquals(new Chunk(20, 19, 1), chunk);
        chunk = staticForLoop.getNextChunk(0);
        Assert.assertEquals(new Chunk(00, 0, 1), chunk);
    }

    @Test
    public void testSimpleStaticForLoop() {
        System.out.println();
        OMPDirectives.parallel(10, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.staticFor(new ForLoop() {

                    @Override
                    public void execute(Chunk chunk) {
                        System.out.println("OMP thread " + OMPRuntime.getThreadNum() + " running chunk " + chunk);
                    }
                }, 10, 0, 1, 2, false);
            }
        });
    }

    @Test
    public void testSimpleStaticForLoopDefaultChunk() {
        System.out.println();
        OMPDirectives.parallel(2, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.staticFor(new ForLoop() {

                    @Override
                    public void execute(Chunk chunk) {
                        System.out.println("OMP thread " + OMPRuntime.getThreadNum() + " running chunk " + chunk);
                    }
                }, 0, 4, 1, true);
            }
        });
    }

    @Test
    public void testSimpleDynamicLoop() {
        System.out.println();
        OMPDirectives.parallel(10, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.dynamicFor(new ForLoop() {

                    @Override
                    public void execute(Chunk chunk) {
                        System.out.println("OMP thread " + OMPRuntime.getThreadNum() + " running chunk " + chunk);
                    }
                }, 0, 40, 1, 2, false);
            }
        });
    }

    @Test
    public void testDirectOrdered() {
        System.out.println();
        OMPDirectives.parallel(10, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.staticFor(new ForLoop() {

                    @Override
                    public void execute(Chunk chunk) {
                        for (int i = chunk.getFirstIndex(); i <= chunk.getLastIndex(); i += chunk.getStep()) {
                            OrderedLock lock = OMPDirectives.getOrderedLock();
                            lock.lock(i);
                            try {
                                System.out.println("Running iteration " + i);
                            } finally {
                                lock.unlock(i);
                            }
                        }
                    }
                }, 0, 40, 1, 2, false);
            }
        });
    }

    @Test
    public void testInverseOrdered() {
        System.out.println();
        OMPDirectives.parallel(10, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.staticFor(new ForLoop() {

                    @Override
                    public void execute(Chunk chunk) {
                        for (int i = chunk.getFirstIndex(); i >= chunk.getLastIndex(); i -= chunk.getStep()) {
                            OrderedLock lock = OMPDirectives.getOrderedLock();
                            lock.lock(i);
                            try {
                                System.out.println("Running iteration " + i);
                            } finally {
                                lock.unlock(i);
                            }
                        }
                    }
                }, 40, 0, 1, 2, true);
            }
        });
    }
}
