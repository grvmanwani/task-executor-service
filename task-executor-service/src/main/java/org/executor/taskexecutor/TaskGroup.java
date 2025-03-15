package org.executor.taskexecutor;

import java.util.UUID;

public record TaskGroup(UUID groupUUID) {
    public TaskGroup{
        if(groupUUID == null){
            throw new IllegalArgumentException("Group UUID must not be null");
        }
    }
}
