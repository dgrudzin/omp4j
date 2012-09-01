package ru.unn.omp4j.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class ExampleUtils {

    private static Map<Integer, double[]> matrixes = new HashMap<Integer, double[]>();
    private static Map<Integer, double[]> vectors = new HashMap<Integer, double[]>();

    public static double[] createRandomMatrix(int n) {

        double[] matrix = matrixes.get(n);
        if (matrix == null) {
            matrix = new double[n * n];

            Random random = new Random();
            for (int i = 0; i < n; i++) {
                double val = random.nextInt(100) + random.nextDouble();
                for (int j = 0; j < n; j++) {
                    matrix[i * n + j] = val;
                }
            }
            matrixes.put(n, matrix);
        }
        return matrix;
    }

    public static double[] createRandomVector(int n) {
        double[] vector = vectors.get(n);
        if (vector == null) {
            vector = new double[n];
            Random random = new Random();

            //Random filling
            for (int i = 0; i < n; i++) {
                vector[i] = random.nextInt(100) + random.nextDouble();
            }
            vectors.put(n, vector);
        }
        return vector;
    }

    public static boolean arrayEquals(double[] a1, double[] a2) {
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    private ExampleUtils() {
    }
}
