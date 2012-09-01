package ru.unn.omp4j.parser;

/**
 * Thrown when some parsing error occurs.
 * 
 * @author Dmitry Grudzinskiy
 *
 */
public class OMPParserException extends Exception {

    private static final long serialVersionUID = 7285053943825584993L;

    public OMPParserException() {
    }

    public OMPParserException(String message) {
        super(message);
    }

    public OMPParserException(Throwable cause) {
        super(cause);
    }

    public OMPParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
