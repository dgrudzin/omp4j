package ru.unn.omp4j.examples;

import java.util.Map;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.task.ParallelTask;

public class MatrixMultBlock implements Example {

    @Override
    public long parallel(Map<String, String> props) {

        if (!props.containsKey("size")) {
            throw new IllegalArgumentException("No 'size' property specified");
        }
        final int n = Integer.valueOf(props.get("size"));
        final double[] matrix1 = ExampleUtils.createRandomMatrix(n);
        final double[] matrix2 = ExampleUtils.createRandomMatrix(n);
        final int numThreads = 4;
        System.out.println("Parallel matrix multiplication for n = " + n);
        final double[] resultMatrix = new double[n * n];
        
        final int gridSize = new Double(Math.sqrt(numThreads)).intValue();
        final int blockSize = n / gridSize;

        long start = System.currentTimeMillis();
        OMPDirectives.parallel(numThreads, new ParallelTask() {
            public void execute() {
                    int ThreadID = OMPRuntime.getThreadNum();
                    int RowIndex = ThreadID / gridSize;
                    int ColIndex = ThreadID % gridSize;
                    
                    for (int iter = 0; iter < gridSize; iter++) {
                        for (int i = RowIndex * blockSize; i < (RowIndex + 1) * blockSize; i++)
                            for (int j = ColIndex * blockSize; j < (ColIndex + 1) * blockSize; j++)
                                for (int k = iter * blockSize; k < (iter + 1) * blockSize; k++)
                                    resultMatrix[i * n + j] += matrix1[i * n + k] * matrix2[k * n + j];
                } 
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
