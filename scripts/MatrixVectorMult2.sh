#!/bin/bash
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv false size=1000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=2000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=3000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=4000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=5000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=6000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=7000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=8000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=9000 numThreads=2
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=10000 numThreads=2
java -server -Xms8000M -Xmx8000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=12000 numThreads=2
java -server -Xms8000M -Xmx8000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.MatrixVectorMult 3 ./MatrixVectorMult.csv true size=15000 numThreads=2
