package ru.unn.omp4j.task;

public class TaskFactory {

    public static TiedTask newTiedTask(ParallelTask task) {
        return new TiedTask(task, null);
    }

    public static UntiedTask newUntiedTask(ParallelTask task) {
        return new UntiedTask(task, null);
    }
    
    private TaskFactory() {
    }
}
