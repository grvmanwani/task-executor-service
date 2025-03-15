package org.executor.taskexecutor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class TaskExecutorImpl implements TaskExecutor{

    private final ExecutorService executor;
    private final Map<UUID, Semaphore> taskGroupLocks;
    private final BlockingQueue<Task<?>> taskQueue;
    private final Map<UUID, CompletableFuture<Object>> taskResults;

    /*
    maxThreads ensures max number of concurrent tasks.
     */
    public TaskExecutorImpl(int maxThreads) {
        this.executor = Executors.newFixedThreadPool(maxThreads);
        this.taskGroupLocks= new ConcurrentHashMap<>();
        this.taskQueue=new LinkedBlockingQueue<>();
        this.taskResults=new ConcurrentHashMap<>();
        startTaskProcessor();
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        CompletableFuture<T> future= new CompletableFuture<>();
        taskResults.put(task.taskUUID(),(CompletableFuture<Object>) future);
        taskQueue.offer(task);
        return future;
    }

    private void startTaskProcessor(){
        new Thread(()->{
            while (!executor.isShutdown()){
                try{
                    Task<?> task=taskQueue.take(); //Take next task in order
                    Semaphore semaphore=taskGroupLocks.computeIfAbsent(
                            task.taskGroup().groupUUID(),
                            k -> new Semaphore(1) // only one task per thread group
                    );

                    executor.submit(()->
                            {
                                try {
                                    semaphore.acquire();
                                    Object result=task.taskAction().call();
                                    System.out.println("Executed Task Group: "+task.taskGroup().groupUUID());
                                    System.out.println("Executed Task : "+task.taskUUID());
                                    CompletableFuture<Object> future=taskResults.remove(task.taskUUID());

                                    if(future!=null){
                                        future.complete(result);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    semaphore.release();
                                }
                            }
                    );
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Override
    public void shutdown() {
        System.out.println("Shutting down Executor");
        executor.shutdown();
        try{
            if(!executor.awaitTermination(5,TimeUnit.SECONDS)){
                executor.shutdownNow();
                System.out.println("Shut down success post awaiting");
            }else{
                System.out.println("Shut down Success");
            }
        }catch (InterruptedException e){
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
