package com.kyle.template.todo.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyle.template.todo.external.dto.repsonse.TaskCreateResponse;
import com.kyle.template.todo.external.dto.request.TaskCreateRequest;
import com.kyle.template.todo.external.interfaces.TaskApi;
import com.kyle.template.todo.model.entity.Task;
import com.kyle.template.todo.model.entity.User;
import com.kyle.template.todo.model.enums.TaskStatus;
import com.kyle.template.todo.repository.TaskRepository;
import com.kyle.template.todo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TaskServiceImpl implements TaskApi {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskCreateResponse createTask(String userId, TaskCreateRequest request) {
        log.info("Create task with title {}", request.getTitle());

        if (userRepository.findByUserId(userId).isEmpty()) {
            log.error("User with ID {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }

        String username = userRepository.findByUserId(userId)
                .map(User::getFullName)
                .orElseThrow(() -> {
                    log.error("Full name for user ID {} not found", userId);
                    return new IllegalArgumentException("Full name not found");
                });

        String taskId = generateTaskId();
        Task task = Task.builder()
                .taskId(taskId)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.PENDING)
                .assignedUserId(userId)
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskCreateResponse.builder()
                .taskId(savedTask.getTaskId())
                .title(savedTask.getTitle())
                .description(savedTask.getDescription())
                .status(savedTask.getStatus())
                .assignedUsername(username)
                .createdAt(savedTask.getCreatedAt())
                .updatedAt(savedTask.getUpdatedAt())
                .build();
    }

    @Override
    public List<TaskCreateResponse> getUserTask(String userId) {
        log.info("Get tasks for user ID {}", userId);

        if (userRepository.findByUserId(userId).isEmpty()) {
            log.error("User with ID {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }

        String username = userRepository.findByUserId(userId)
                .map(User::getFullName)
                .orElseThrow(() -> {
                    log.error("Full name for user ID {} not found", userId);
                    return new IllegalArgumentException("Full name not found");
                });

        List<Task> tasks = taskRepository.findByAssignedUserId(userId);

        return tasks.stream()
                .map(task -> TaskCreateResponse.builder()
                        .taskId(task.getTaskId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .assignedUsername(username)
                        .createdAt(task.getCreatedAt())
                        .updatedAt(task.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    //Helpers
    private String generateTaskId() {
        // Implement a proper task ID generation logic
        return "task-" + UUID.randomUUID();
    }

}
