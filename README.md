# executor

1. Tasks can be submitted concurrently. Task submission will not block the submitter.
2. Tasks are executed asynchronously and concurrently. Maximum allowed concurrency
is restricted.
3. Once task is finished, its results can be retrieved from the Future received during task
submission.
4. The order of tasks is preserved.
   a. The first task submitted must be the first task started.
   b. The task result should be available as soon as possible after the task completes.
5. Tasks sharing the same TaskGroup will not run concurrently.
