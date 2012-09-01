package ru.unn.omp4j.test;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import ru.unn.omp4j.task.OMPTask;
import ru.unn.omp4j.task.ParallelTask;
import ru.unn.omp4j.task.TaskFactory;
import ru.unn.omp4j.task.TiedTask;

public class TaskTest {

    @Test
    public void testCreateRootTask() {
        ParallelTask testTask = new ParallelTask() {

            @Override
            public void execute() {
                System.out.println("I am the first omp4j task");
            }
        };

        OMPTask manTask = TaskFactory.newTiedTask(testTask);

        manTask.addChild(new TiedTask(new ParallelTask() {

            @Override
            public void execute() {
                System.out.println("I am the first child task");
            }
        }, manTask));

        Assert.assertNull("Root task has no parent", manTask.getParent());
        Set<OMPTask> children = manTask.getChildren();
        Assert.assertEquals("Only one child", 1, children.size());
    }
}
