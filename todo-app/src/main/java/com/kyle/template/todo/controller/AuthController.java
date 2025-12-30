package com.kyle.template.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyle.template.todo.external.dto.repsonse.UserCreateResponse;
import com.kyle.template.todo.external.dto.repsonse.UserLoginResponse;
import com.kyle.template.todo.external.dto.request.UserCreateRequest;
import com.kyle.template.todo.external.dto.request.UserLoginRequest;
import com.kyle.template.todo.external.interfaces.AuthApi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApi authApi;

    @PostMapping("/register")
    public ResponseEntity<UserCreateResponse> register(
            @Valid @RequestBody UserCreateRequest request) {
        log.info("Received user registration request for username: {}", request.getUsername());

        UserCreateResponse response = authApi.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);
        log.info("Login attempt for username: {} from ip: {}", request.getUsername(), ipAddress);

        UserLoginResponse response = authApi.loginUser(request, ipAddress, deviceInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String getClientIp(HttpServletRequest request) {
        // X-Forwarded-For header is set by proxies/load balancers
        // Contains original client IP (may have multiple IPs if multiple proxies)
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // Take first IP (original client) if multiple proxies added IPs
            return forwardedFor.split(",")[0].trim();
        }
        // Fallback for direct connections (local development)
        return request.getRemoteAddr();
    }
}
