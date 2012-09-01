package ru.unn.omp4j.examples;

import java.util.Map;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.directives.workshare.Chunk;
import ru.unn.omp4j.directives.workshare.ForLoop;
import ru.unn.omp4j.task.ParallelTask;

public class MatrixVectorMult implements Example {

    @Override
    public long parallel(Map<String, String> props) {
        if (!props.containsKey("size") || !props.containsKey("numThreads")) {
            throw new IllegalArgumentException("No 'size' or 'numThreads' properties specified");
        }
        int n = Integer.valueOf(props.get("size"));
        final int numThreads = Integer.valueOf(props.get("numThreads"));

        System.out.println("Parallel matrix vector multiplication for n = " + n +  " numThreads =  " + numThreads);
        
        final double[] matrix = ExampleUtils.createRandomMatrix(n);
        final double[] vector = ExampleUtils.createRandomVector(n);
        final double[] result = new double[vector.length];

        long start = System.currentTimeMillis();

        OMPDirectives.parallel(numThreads, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.dynamicFor(new ForLoop() {

                    @Override
                    public void execute(Chunk chunk) {
                        int start = chunk.getFirstIndex();
                        int end = chunk.getLastIndex();
                        int step = chunk.getStep();
                        for (int i = start; i <= end; i += step) {
                            for (int j = 0; j < vector.length; j++) {
                                result[i] += matrix[i * vector.length + j] * vector[j];
                            }
                        }
                    }
                }, 0, vector.length - 1, 1, vector.length / numThreads, true);
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

        System.out.println("Sequental matrix vector multiplication for n = " + n);
        
        double[] matrix = ExampleUtils.createRandomMatrix(n);
        double[] vector = ExampleUtils.createRandomVector(n);
        double[] result = new double[vector.length];

        long start = System.currentTimeMillis();
        for (int i = 0; i < vector.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i * vector.length + j] * vector[j];
            }
        }
        return System.currentTimeMillis() - start;
    }
}
