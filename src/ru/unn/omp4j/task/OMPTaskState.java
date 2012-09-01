package ru.unn.omp4j.task;

import ru.unn.omp4j.thread.Level;

public abstract class OMPTaskState {

    public static final OMPTaskState STATE_READY = new OMPTaskState("READY") {

        @Override
        public boolean perform(OMPTask task, Level level) {
            task.execute();
            return true;
        }
    };

    public static final OMPTaskState STATE_IN_BARRIER = new OMPTaskState("IN BARRIER") {

        @Override
        public boolean perform(OMPTask task, Level level) {
            return !task.runFromBarrier(level);
        }
    };

    public static final OMPTaskState STATE_SCHEDULING_POINT = new OMPTaskState("SCHEDULING POINT") {

        @Override
        public boolean perform(OMPTask task, Level level) {
            return task.runFromSchedulingPoint(level);
        }
    };

    public static final OMPTaskState STATE_TASKWAIT = new OMPTaskState("TASK WAIT") {

        @Override
        public boolean perform(OMPTask task, Level level) {
            return task.runFromTaskWait(level);
        }
    };

    public static final OMPTaskState STATE_COMPLETED = new DefaultTaskState("COMPLETED",
            "Completed Task cannot be executed again");

    public static final OMPTaskState STATE_IN_PROGRESS = new DefaultTaskState("IN PROGRESS",
            "Running task cannot be executed again");

    private String name;

    public OMPTaskState(String name) {
        this.name = name;
    }

    public abstract boolean perform(OMPTask task, Level level);

    @Override
    public String toString() {
        return name;
    }

    private static class DefaultTaskState extends OMPTaskState {

        private String msg;

        private DefaultTaskState(String name, String msg) {
            super(name);
            this.msg = msg;
        }

        @Override
        public boolean perform(OMPTask task, Level level) {
            throw new IllegalStateException(msg);
        }
    }
}
