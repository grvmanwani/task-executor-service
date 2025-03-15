package org.executor;

import org.executor.taskexecutor.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskExecutorImplTest {

    private TaskExecutor taskExecutor;

    @BeforeEach
    void setUp() {
        taskExecutor=new TaskExecutorImpl(2);
    }

    @AfterEach
    void tearDown() {
        taskExecutor.shutdown();
    }

    @Test
    void testTaskSubmissionAndExecution() throws ExecutionException, InterruptedException {
        TaskGroup taskGroup=new TaskGroup(UUID.randomUUID());

        Future<String> future=taskExecutor.submitTask(new Task<>(
                UUID.randomUUID(),
                taskGroup,
                TaskType.READ,
                () -> "Test Task Executed"
        ));

        assertEquals("Test Task Executed",future.get());
    }

    @Test
    void testMultipleTaskExecution() throws ExecutionException, InterruptedException, TimeoutException {
        TaskGroup taskGroup=new TaskGroup(UUID.randomUUID());

        Future<String> future1=taskExecutor.submitTask(new Task<>(
                UUID.randomUUID(),
                taskGroup,
                TaskType.READ,
                () -> "Task 1"
        ));

        Future<String> future2=taskExecutor.submitTask(new Task<>(
                UUID.randomUUID(),
                taskGroup,
                TaskType.WRITE,
                () -> "Task 2"
        ));

        assertEquals("Task 1",future1.get(1, TimeUnit.SECONDS));
        assertEquals("Task 2",future2.get(1, TimeUnit.SECONDS));
    }


    @Test
    void testTaskInSameGroupShouldNotRunConcurrently() throws ExecutionException, InterruptedException {
        TaskGroup taskGroup=new TaskGroup(UUID.randomUUID());

        Future<String> future1=taskExecutor.submitTask(new Task<>(
                UUID.randomUUID(),
                taskGroup,
                TaskType.READ,
                () -> {
                    Thread.sleep(500);
                    return "Task 1";
                }
        ));

        Future<String> future2=taskExecutor.submitTask(new Task<>(
                UUID.randomUUID(),
                taskGroup,
                TaskType.WRITE,
                () -> "Task 2"
        ));

        assertEquals("Task 1",future1.get());
        assertEquals("Task 2",future2.get());
    }

//    @Test
//    void shutdown() {
////
////        Task<String> task=new Task<>(
////                UUID.randomUUID(),
////                new TaskGroup(UUID.randomUUID()),
////                TaskType.READ,
////                () -> "Task Completed"
////        );
////
////        Future<String> future=taskExecutor.submitTask(task);
////        assertNotNull(future);
//
//        taskExecutor.shutdown();
//
////        assertTrue(executorServiceIsShutDown());
//       assertThrows(RejectedExecutionException.class,()->{
//            taskExecutor.submitTask(new Task<Object>(
//                    UUID.randomUUID(),
//                    new TaskGroup(UUID.randomUUID()),
//                    TaskType.READ,
//                    () -> "New Task"
//            ));
//        });
//
////        assertNotNull(exception);
//
////        try{
////            assertEquals("Task Completed",future.get());
////        } catch (Exception e){
////            fail("Task Execution failed unexpectedly");
////        }
//    }
//
//    private boolean executorServiceIsShutDown(){
//
//        try{
//            taskExecutor.submitTask(new Task<>(
//                    UUID.randomUUID(),
//                    new TaskGroup(UUID.randomUUID()),
//                    TaskType.READ,
//                    () -> "Check"
//            ));
//
//            return false;
//        }catch (RejectedExecutionException e){
//            return true;
//        }
//
//    }
}