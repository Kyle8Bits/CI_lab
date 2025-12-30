package com.kyle.template.todo.external.interfaces;

import com.kyle.template.todo.external.dto.repsonse.UserCreateResponse;
import com.kyle.template.todo.external.dto.repsonse.UserLoginResponse;
import com.kyle.template.todo.external.dto.request.UserCreateRequest;
import com.kyle.template.todo.external.dto.request.UserLoginRequest;

public interface AuthApi {
    UserCreateResponse registerUser(UserCreateRequest request);
    UserLoginResponse loginUser(UserLoginRequest request, String ipAddress,String deviceInfo);
}
