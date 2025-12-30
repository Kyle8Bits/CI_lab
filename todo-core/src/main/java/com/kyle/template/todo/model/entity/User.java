package com.kyle.template.todo.model.entity;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    
    @Indexed(unique = true)
    @Field("userId")
    private String userId;

    @Field("fullName")
    private String fullName;

    @Field("taskStorageId")
    private List<String> taskStorageId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTaskStorageId() {
        return taskStorageId;
    }

    public void setTaskStorageId(List<String> taskStorageId) {
        this.taskStorageId = taskStorageId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
