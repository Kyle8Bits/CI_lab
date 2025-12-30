package com.kyle.template.todo.external.dto.repsonse;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    
    private String userId;
    private String username;
    private String fullName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
