package com.kyle.template.todo.external.dto.repsonse;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginResponse {

    private String accessToken;

    /**
     * Refresh Token for token renewal (2.3.3)
     */
    private String refreshToken;

    /**
     * Token type (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access token expiration time in seconds
     */
    private Long expiresIn;

    /**
     * Refresh token expiration time in seconds
     */
    private Long refreshExpiresIn;
}
