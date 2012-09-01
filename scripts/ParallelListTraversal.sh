#!/bin/bash
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv false size=3000 numThreads=2 maxTasks=10 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=50 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=100 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=200 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=300 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=400 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=1000 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=1500 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=2000 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=3000 tied=true
java -server -Xms4000M -Xmx4000M  -XX:+UseParallelGC -cp ./omp4j.jar ru.unn.omp4j.examples.ExampleRunner ru.unn.omp4j.examples.ParallelListTraversal 3 ./ParallelList.csv true size=3000 numThreads=2 maxTasks=5000 tied=true

