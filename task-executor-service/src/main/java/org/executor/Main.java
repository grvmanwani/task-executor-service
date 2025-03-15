package org.executor;

import org.executor.taskexecutor.*;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        TaskExecutor executor=new TaskExecutorImpl(4);

        TaskGroup group1=new TaskGroup(UUID.randomUUID());
        TaskGroup group2=new TaskGroup(UUID.randomUUID());

        Task<String> task1= new Task<>(UUID.randomUUID(), group1, TaskType.READ, ()-> "Task 1 Executed");

        Task<String> task2= new Task<>(UUID.randomUUID(), group1, TaskType.WRITE, () -> "Task 2 Executed");
        Task<String> task3= new Task<>(UUID.randomUUID(), group2, TaskType.WRITE, () -> "Task 3 Executed");
        Task<String> task4= new Task<>(UUID.randomUUID(), group2, TaskType.WRITE, () -> "Task 4 Executed");

        try{
            Future<String> result1=executor.submitTask(task1);
            Future<String> result2=executor.submitTask(task2);
            Future<String> result3=executor.submitTask(task3);
            Future<String> result4=executor.submitTask(task4);

            System.out.println("Result 1: " + result1.get(1, TimeUnit.SECONDS));
            System.out.println("Result 2: " + result2.get(1, TimeUnit.SECONDS));
            System.out.println("Result 3: " + result3.get(1, TimeUnit.SECONDS));
            System.out.println("Result 4: " + result4.get(1, TimeUnit.SECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
    }
}