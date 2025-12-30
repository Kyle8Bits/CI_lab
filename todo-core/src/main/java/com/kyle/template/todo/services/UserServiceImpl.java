package com.kyle.template.todo.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyle.template.todo.external.dto.repsonse.UserCreateResponse;
import com.kyle.template.todo.external.dto.request.UserCreateRequest;
import com.kyle.template.todo.external.interfaces.UserApi;
import com.kyle.template.todo.model.entity.User;
import com.kyle.template.todo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserApi {

    private final UserRepository userRepository;

    @Override
    public UserCreateResponse createProfile(UserCreateRequest request) {
        log.info("Creating user account");

        User user = User.builder()
                .userId(generateUserId())
                .fullName(request.getFullName())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User account created with ID: {}", savedUser.getUserId());

        return UserCreateResponse.builder()
                .userId(savedUser.getUserId())
                .username(request.getUsername())
                .fullName(savedUser.getFullName())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }


    //Helpers

    private String generateUserId() {
        // Implement a proper user ID generation logic
        return "user-" + UUID.randomUUID();
    }
}
