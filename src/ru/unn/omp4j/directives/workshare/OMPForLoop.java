package ru.unn.omp4j.directives.workshare;

import ru.unn.omp4j.task.OMPParameters;

public abstract class OMPForLoop implements ForLoop {

    private OMPParameters sharedVars;
    private OMPParameters privateVars;

    public OMPForLoop(OMPParameters sharedVars, OMPParameters privateVars) {
        super();
        this.sharedVars = sharedVars;
        this.privateVars = privateVars;
    }

    @Override
    public void execute(Chunk chunk) {
        execute(sharedVars, privateVars, chunk);
    }

    protected abstract void execute(OMPParameters sharedVars, OMPParameters privateVars, Chunk chunk);
}
