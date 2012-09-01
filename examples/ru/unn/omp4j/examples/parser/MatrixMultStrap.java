package ru.unn.omp4j.examples.parser;

import org.apache.log4j.xml.DOMConfigurator;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.directives.workshare.Chunk;
import ru.unn.omp4j.directives.workshare.OMPForLoop;
import ru.unn.omp4j.examples.ExampleUtils;
import ru.unn.omp4j.task.OMPParallelTask;
import ru.unn.omp4j.task.OMPParameters;

public class MatrixMultStrap {
    public static void main(String[] args) {
        DOMConfigurator.configure("./conf/omp-log4j.xml");

        final int n = 100;
        final double[] matrix1 = ExampleUtils.createRandomMatrix(n);
        final double[] matrix2 = ExampleUtils.createRandomMatrix(n);
        final double[] resultMatrix = ExampleUtils.createRandomMatrix(n);
        int NestedThreadsNum = 2;
        OMPRuntime.setNumThreads(NestedThreadsNum);
        final OMPParameters shared_1 = new OMPParameters();
        final OMPParameters private_1 = new OMPParameters();

        OMPDirectives.parallel(new OMPParallelTask(shared_1, private_1) {

            protected void execute(OMPParameters sharedVars, OMPParameters privateVars) {

                final OMPParameters shared_2 = new OMPParameters();
                final OMPParameters private_2 = new OMPParameters();

                OMPDirectives.staticFor(new OMPForLoop(shared_2, private_2) {

                    protected void execute(OMPParameters sharedVars, OMPParameters privateVars, Chunk chunk) {

                        for (int i = chunk.getFirstIndex(); i <= chunk.getLastIndex(); i++) {
                            final int row = i;
                            final OMPParameters shared_3 = new OMPParameters();
                            final OMPParameters private_3 = new OMPParameters();

                            OMPDirectives.parallel(new OMPParallelTask(shared_3, private_3) {

                                protected void execute(OMPParameters sharedVars, OMPParameters privateVars) {
                                    final OMPParameters shared_4 = new OMPParameters();
                                    final OMPParameters private_4 = new OMPParameters();

                                    OMPDirectives.staticFor(new OMPForLoop(shared_4, private_4) {

                                        protected void execute(OMPParameters sharedVars, OMPParameters privateVars,
                                                Chunk chunk) {
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

    }

}
