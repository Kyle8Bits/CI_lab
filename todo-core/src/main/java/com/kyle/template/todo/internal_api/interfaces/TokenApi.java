package com.kyle.template.todo.internal_api.interfaces;

import com.kyle.template.todo.internal_api.dto.TokenInternalDto;

public interface TokenApi {

    TokenInternalDto generateTokens(String userId, String username, String ipAddress, String deviceInfo);

    String extractUserIdFromAccessToken(String token);
}
