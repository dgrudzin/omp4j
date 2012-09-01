package ru.unn.omp4j.test;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.task.ParallelTask;

public class OMPTest {

    @Before
    public void setUp() {
        DOMConfigurator.configure("./conf/omp-log4j.xml");
    }

    @Test
    public void testInitialCreation() {

        OMPDirectives.parallel(2, new ParallelTask() {

            @Override
            public void execute() {

                System.out.println("I am thread " + OMPRuntime.getThreadNum());

                OMPDirectives.barrier();
                System.out.println("I am thread " + OMPRuntime.getThreadNum() + " after barrier");
            }
        });
    }

    @Test
    public void testNested() {
        System.out.println("Testing nested");
        OMPDirectives.parallel(1, new ParallelTask() {

            @Override
            public void execute() {
                System.out.println("I am thread " + OMPRuntime.getThreadNum() + " level " + OMPRuntime.getLevel());
                OMPDirectives.barrier();
                OMPDirectives.parallel(3, new ParallelTask() {

                    @Override
                    public void execute() {
                        System.out.println("I am thread " + OMPRuntime.getThreadNum() + " level "
                                + OMPRuntime.getLevel());
                        OMPDirectives.barrier();
                    }
                });
                System.out.println("I am thread " + OMPRuntime.getThreadNum() + " level " + OMPRuntime.getLevel()
                        + " finished executing nested region");
            }
        });
    }

    @Test
    public void testCreateTask() {
        System.out.println();
        System.out.println("Testing task creation");

        OMPDirectives.parallel(4, new ParallelTask() {

            @Override
            public void execute() {
                System.out.println("I am thread " + OMPRuntime.getThreadNum() + " level " + OMPRuntime.getLevel());
                for (int i = 1; i < 6; i++) {

                    final int j = i;
                    OMPDirectives.task(new ParallelTask() {

                        @Override
                        public void execute() {
                            System.out.println("I am inside task " + j + " OMP thread " + OMPRuntime.getThreadNum());
                        }
                    }, false);
                }

                OMPDirectives.taskwait();

            }
        });
    }

    @Test
    public void testSingle() {
        System.out.println();
        OMPDirectives.parallel(4, new ParallelTask() {

            @Override
            public void execute() {
                OMPDirectives.singleTask(new ParallelTask() {

                    @Override
                    public void execute() {
                        System.out.println("I am thread " + OMPRuntime.getThreadNum() + " running single task 1");
                    }
                }, false);

                OMPDirectives.singleTask(new ParallelTask() {

                    @Override
                    public void execute() {
                        System.out.println("I am thread " + OMPRuntime.getThreadNum() + " running single task 2");
                    }
                }, false);
            }
        });
    }
}
