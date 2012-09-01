package ru.unn.omp4j.util;

import org.apache.commons.logging.Log;

import ru.unn.omp4j.thread.Level;

public final class LoggingUtils {

    public static void debug(Log log, String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }

    public static void debug(Log log, Level level, String message) {
        if (log.isDebugEnabled()) {
            log.debug("OMP thread " + level.getThreadNum() + " at level " + level.getRegion().getLevelNumber() + " "
                    + message);
        }
    }

    private LoggingUtils() {
    }
}
