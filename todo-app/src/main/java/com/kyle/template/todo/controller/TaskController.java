package com.kyle.template.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyle.template.todo.external.dto.repsonse.TaskCreateResponse;
import com.kyle.template.todo.external.dto.request.TaskCreateRequest;
import com.kyle.template.todo.external.interfaces.TaskApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskApi taskApi;

    @PostMapping("/create")
    public ResponseEntity<TaskCreateResponse> create(
            @Valid @RequestBody TaskCreateRequest request) {
        // get authenticated userId from SecurityContext (set by JweAuthenticationFilter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof String) {
                userId = (String) principal;
            }
        }

        if (userId == null) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Received task creation request for user ID: {}", userId);
        TaskCreateResponse response = taskApi.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping({"", "/user-tasks"})
    public ResponseEntity<java.util.List<TaskCreateResponse>> getUserTasks() {
        // get authenticated userId from SecurityContext (set by JweAuthenticationFilter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof String) {
                userId = (String) principal;
            }
        }

        if (userId == null) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Received request to get tasks for user ID: {}", userId);
        java.util.List<TaskCreateResponse> responses = taskApi.getUserTask(userId);
        return ResponseEntity.ok(responses);
    }

}
