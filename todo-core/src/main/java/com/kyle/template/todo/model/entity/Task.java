package com.kyle.template.todo.model.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.kyle.template.todo.model.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "tasks")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    @Indexed(unique = true)
    @Field("taskId")
    private String taskId;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("status")
    private TaskStatus status;

    @Field("assignedUserId")
    private String assignedUserId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
