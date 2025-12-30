package com.kyle.template.todo.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kyle.template.todo.TestResultLogger;
import com.kyle.template.todo.exceptions.AppException;
import com.kyle.template.todo.external.dto.request.UserLoginRequest;
import com.kyle.template.todo.internal_api.interfaces.TokenApi;
import com.kyle.template.todo.model.entity.AuthEntity;
import com.kyle.template.todo.repository.AuthRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ExtendWith({MockitoExtension.class, TestResultLogger.class})
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenApi tokenApi; // or TokenService, depending on your design

    @InjectMocks
    private AuthServiceImpl authService; // change to your class name

    @Test
    @DisplayName("loginFailsWhenUserNotFound")
    void loginFailsWhenUserNotFound() {
        when(authRepository.findByUsernameIgnoreCase("kyle"))
                .thenReturn(Optional.empty());

        UserLoginRequest request = UserLoginRequest.builder()
                .username("kyle")
                .password("Kyle2003@")
                .build();

        AppException ex = assertThrows(
                AppException.class,
                () -> authService.loginUser(request, "127.0.0.1", "SomeDeviceInfo")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertTrue(ex.getMessage().contains("Invalid credentials"));
    }

    @Test
    void loginFailsWhenPasswordWrong() {
        AuthEntity auth = AuthEntity.builder()
                .username("kylemai2611")
                .passwordHash("hashedPassword")
                .build();

        when(authRepository.findByUsernameIgnoreCase("kylemai2611"))
                .thenReturn(Optional.of(auth));

        when(passwordEncoder.matches("WrongPassword", "hashedPassword"))
                .thenReturn(false);

        UserLoginRequest request = UserLoginRequest.builder()
                .username("kylemai2611")
                .password("WrongPassword")
                .build();

        AppException ex = assertThrows(
                AppException.class,
                () -> authService.loginUser(request, "127.0.0.1", "SomeDeviceInfo")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }
}
