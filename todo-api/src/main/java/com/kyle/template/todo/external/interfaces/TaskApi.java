package com.kyle.template.todo.external.interfaces;

import java.util.List;

import com.kyle.template.todo.external.dto.repsonse.TaskCreateResponse;
import com.kyle.template.todo.external.dto.request.TaskCreateRequest;

public interface TaskApi {
    TaskCreateResponse createTask(String userId, TaskCreateRequest request);

    List<TaskCreateResponse> getUserTask(String userId);
}
