package ru.unn.omp4j.task;

import java.util.HashMap;
import java.util.Map;

public class OMPParameters {

    private final Map<String, Object> parameters = new HashMap<String, Object>();

    public OMPParameters() {
    }

    public void addParameter(String name, Object value) {
        parameters.put(name, value);
    }

    public Object getParameter(String name) {
        return parameters.get(name);
    }
}
