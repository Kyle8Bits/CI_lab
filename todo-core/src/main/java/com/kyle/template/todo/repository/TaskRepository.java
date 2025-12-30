package com.kyle.template.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kyle.template.todo.model.entity.Task;
import com.kyle.template.todo.model.enums.TaskStatus;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    Optional<Task> findByTaskId(String taskId);

    Optional<Task> findByTitle(String title);

    Optional<Task> findByStatus(TaskStatus status);

    List<Task> findByAssignedUserId(String userId);
}
