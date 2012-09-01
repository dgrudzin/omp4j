package ru.unn.omp4j.examples;

import java.util.Map;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.directives.workshare.Chunk;
import ru.unn.omp4j.directives.workshare.ForLoop;
import ru.unn.omp4j.task.ParallelTask;

public class MatrixMultStrap implements Example {

    @Override
    public long parallel(Map<String, String> props) {
        if (!props.containsKey("size")) {
            throw new IllegalArgumentException("No 'size' property specified");
        }
        final int n = Integer.valueOf(props.get("size"));
        final double[] matrix1 = ExampleUtils.createRandomMatrix(n);
        final double[] matrix2 = ExampleUtils.createRandomMatrix(n);
        final int numThreads = Integer.valueOf(props.get("numThreads"));
        System.out.println("Parallel matrix multiplication for n = " + n);
        final double[] resultMatrix = new double[n * n];

        OMPRuntime.setNumThreads(numThreads);
        
        long start = System.currentTimeMillis();
        OMPDirectives.parallel(new ParallelTask() {

            public void execute() {

                OMPDirectives.staticFor(new ForLoop() {

                    public void execute(Chunk chunk) {

                        for (int i = chunk.getFirstIndex(); i <= chunk.getLastIndex(); i++) {
                            final int row = i;

                            OMPDirectives.parallel(new ParallelTask() {

                                public void execute() {

                                    OMPDirectives.staticFor(new ForLoop() {
                                        public void execute(Chunk chunk) {
                                            for (int j = chunk.getFirstIndex(); j <= chunk.getLastIndex(); j++)
                                                for (int k = 0; k < n; k++) {
                                                    resultMatrix[row * n + j] += matrix1[row * n + k]
                                                            * matrix2[k * n + j];
                                                }
                                        }
                                    }, 0, n - 1, 1, false);
                                }
                            });
                        }
                    }
                }, 0, n - 1, 1, false);
            }
        });

        return System.currentTimeMillis() - start;
    }

    @Override
    public long sequental(Map<String, String> props) {
        if (!props.containsKey("size")) {
            throw new IllegalArgumentException("No 'size' property specified");
        }
        int n = Integer.valueOf(props.get("size"));

        System.out.println("Sequental matrix multiplication for n = " + n);

        double[] matrix1 = ExampleUtils.createRandomMatrix(n);
        double[] matrix2 = ExampleUtils.createRandomMatrix(n);
        final double[] resultMatrix = new double[n * n];

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    resultMatrix[i * n + j] += matrix1[i * n + k] * matrix2[k * n + j];
                }
            }
        }
        return System.currentTimeMillis() - start;
    }
}
