package com.kyle.template.todo.services;

import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyle.template.todo.exceptions.AppException;
import com.kyle.template.todo.external.dto.repsonse.UserCreateResponse;
import com.kyle.template.todo.external.dto.repsonse.UserLoginResponse;
import com.kyle.template.todo.external.dto.request.UserCreateRequest;
import com.kyle.template.todo.external.dto.request.UserLoginRequest;
import com.kyle.template.todo.external.interfaces.AuthApi;
import com.kyle.template.todo.external.interfaces.UserApi;
import com.kyle.template.todo.internal_api.dto.TokenInternalDto;
import com.kyle.template.todo.internal_api.interfaces.TokenApi;
import com.kyle.template.todo.model.entity.AuthEntity;
import com.kyle.template.todo.repository.AuthRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuthServiceImpl implements AuthApi {

    private final AuthRepository authRepository;
    private final UserApi userService;
    private final TokenApi tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserCreateResponse registerUser(UserCreateRequest request) {
        log.info("Creating auth account with username: {}", request.getUsername());

        if (authRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            log.warn("Username {} already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        UserCreateResponse userCreateResponse = userService.createProfile(request);


        AuthEntity authEntity = AuthEntity.builder()
                .userId(userCreateResponse.getUserId())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // In real implementation, hash the password
                .build();

        authRepository.save(authEntity);
        log.info("Auth account created for username: {}", request.getUsername());

        return userCreateResponse;
    }

    @Override
    public UserLoginResponse loginUser(UserLoginRequest request, String ipAddress, String deviceInfo) {
     AuthEntity auth = authRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), auth.getPasswordHash())) {
            throw new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Generate tokens
        TokenInternalDto tokens = tokenService.generateTokens(auth.getUserId(), auth.getUsername(), ipAddress, deviceInfo);
        log.info(deviceInfo);

        // Persist refresh token + expiry
        auth.setRefreshToken(tokens.getRefreshToken());
        // store expiry as epoch seconds (Long)
        auth.setRefreshTokenExpiry(tokens.getRefreshTokenExpiry().toInstant(ZoneOffset.UTC).getEpochSecond());
        authRepository.save(auth);

        // Build response
        return UserLoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .expiresIn(tokens.getAccessTokenExpiry() != null ? 
                    java.time.Duration.between(java.time.Instant.now(), tokens.getAccessTokenExpiry().toInstant(ZoneOffset.UTC)).getSeconds() : null)
                .refreshExpiresIn(tokens.getRefreshTokenExpiry() != null ? 
                    java.time.Duration.between(java.time.Instant.now(), tokens.getRefreshTokenExpiry().toInstant(ZoneOffset.UTC)).getSeconds() : null)
                .build();
    }
}
