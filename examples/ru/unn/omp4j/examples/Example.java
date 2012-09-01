package ru.unn.omp4j.examples;

import java.util.Map;

public interface Example {

    long sequental(Map<String, String> props);
    
    long parallel(Map<String, String> props);
}
