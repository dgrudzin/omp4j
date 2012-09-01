#!/bin/bash
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 false size=100 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=300 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=500 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=1000 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=1500 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=2000 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=2500 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=3000 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=5000 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList500 true size=10000 numThreads=2 maxTasks=500

