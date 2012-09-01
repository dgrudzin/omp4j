package ru.unn.omp4j.test;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.task.ParallelTask;

public class SectionsTaskTest {

    @Before
    public void setUp() {
        DOMConfigurator.configure("./conf/omp-log4j.xml");
    }

    @Test
    public void simpleSectionsTaskTest() {
        OMPDirectives.parallel(4, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.sections(new ParallelTask[] { new ParallelTask() {

                    @Override
                    public void execute() {
                        System.out.println("I am thread " + OMPRuntime.getThreadNum() + " running 1 section");
                    }
                }, new ParallelTask() {

                    @Override
                    public void execute() {
                        System.out.println("I am thread " + OMPRuntime.getThreadNum() + " running 2 section");
                    }
                } }, true);
            }
        });
    }
}
