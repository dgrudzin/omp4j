package ru.unn.omp4j.examples;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.task.ParallelTask;

public class ParallelListTraversal implements Example {

    @Override
    public long parallel(Map<String, String> props) {
        if (!props.containsKey("size")) {
            throw new IllegalArgumentException("No 'size' property specified");
        }
        final int n = Integer.valueOf(props.get("size"));
        final int numThreads = Integer.valueOf(props.get("numThreads"));
        final int maxTasks = Integer.valueOf(props.get("maxTasks"));

        final boolean tied = Boolean.valueOf(props.get("tied"));

        final List<Element> list = new LinkedList<Element>();
        for (int i = 0; i < n; i++) {
            list.add(new Element());
        }

        OMPRuntime.setMaxTasks(maxTasks);

        long start = System.currentTimeMillis();
        OMPDirectives.parallel(numThreads, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.singleTask(new ParallelTask() {

                    @Override
                    public void execute() {
                        for (final Element e : list) {
                            OMPDirectives.task(new ParallelTask() {

                                @Override
                                public void execute() {
                                    e.process();
                                }
                            }, tied);
                        }
                    }
                }, false);
            }
        });

        return System.currentTimeMillis() - start;
    }

    @Override
    public long sequental(Map<String, String> props) {
        if (!props.containsKey("size")) {
            throw new IllegalArgumentException("No 'size' property specified");
        }
        final int n = Integer.valueOf(props.get("size"));

        final List<Element> list = new LinkedList<Element>();
        for (int i = 0; i < n; i++) {
            list.add(new Element());
        }

        long start = System.currentTimeMillis();

        for (Element e : list) {
            e.process();
        }
        return System.currentTimeMillis() - start;
    }

    private class Element {

        public void process() {
            // System.out.println("An element is processed by thread " + OMPRuntime.getThreadNum());
            double[] vector = new double[10000];
            Random random = new Random();
            for (int i = 0; i < vector.length; i++) {
                vector[i] = random.nextInt(100) + random.nextDouble();
            }

            double squareSum = 0;
            for (int i = 0; i < vector.length; i++) {
                squareSum += vector[i] * vector[i];
            }
        }
    }
}
