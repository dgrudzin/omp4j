package ru.unn.omp4j.directives.workshare;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.unn.omp4j.task.ParallelTask;

public class Sections {

    private Queue<ParallelTask> tasks = new ConcurrentLinkedQueue<ParallelTask>();

    public Sections(ParallelTask[] tasks) {
        for (ParallelTask task : tasks) {
            this.tasks.offer(task);
        }
    }

    public ParallelTask getNextTask() {
        return tasks.poll();
    }
}
