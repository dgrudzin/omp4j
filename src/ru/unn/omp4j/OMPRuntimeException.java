package ru.unn.omp4j;

public class OMPRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5998985270394743657L;

    public OMPRuntimeException() {
    }

    public OMPRuntimeException(String message) {
        super(message);
    }

    public OMPRuntimeException(Throwable cause) {
        super(cause);
    }

    public OMPRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
