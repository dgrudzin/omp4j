package ru.unn.omp4j.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public final class CollectionUtils {

    public static <U, V> Map<U, V> newHashMap() {
        return new HashMap<U, V>();
    }

    public static <U, V> Map<U, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<U, V>();
    }
    
    public static <U, V> Map<U, V> newLinkedHashMap() {
        return new LinkedHashMap<U, V>();
    }

    public static <U, V> Map<U, V> newTreeMap() {
        return new TreeMap<U, V>();
    }

    public static <U> Set<U> newHashSet() {
        return new HashSet<U>();
    }

    public static <U> Set<U> newLinkedHashSet() {
        return new LinkedHashSet<U>();
    }

    public static <U> List<U> newArrayList() {
        return new ArrayList<U>();
    }

    public static <U, V> void updateSetValuedMap(Map<U, Set<V>> map, U key, V value) {
        Set<V> valueSet = getValueSet(map, key);
        valueSet.add(value);
    }

    public static <U, V> void updateSetValuedMap(Map<U, Set<V>> map, U key, V[] values) {
        Set<V> valueSet = getValueSet(map, key);
        valueSet.addAll(Arrays.asList(values));
    }

    private static <U, V> Set<V> getValueSet(Map<U, Set<V>> map, U key) {
        Set<V> valueSet = map.get(key);
        if (valueSet == null) {
            valueSet = new LinkedHashSet<V>();
            map.put(key, valueSet);
        }
        return valueSet;
    }

    private CollectionUtils() {
    }
}
