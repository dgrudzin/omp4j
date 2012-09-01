package ru.unn.omp4j.task;

public abstract class OMPParallelTask implements ParallelTask {

    private OMPParameters sharedVars;
    private OMPParameters privateVars;

    public OMPParallelTask(OMPParameters sharedVars, OMPParameters privateVars) {
        super();
        this.sharedVars = sharedVars;
        this.privateVars = privateVars;
    }

    @Override
    public void execute() {
        execute(sharedVars, privateVars);
    }

    protected abstract void execute(OMPParameters sharedVars, OMPParameters privateVars);
}
