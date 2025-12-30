package com.kyle.template.todo.external.dto.repsonse;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateResponse {

    private String taskId;
    private String title;
    private String description;
    private String status;
    private String assignedUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
