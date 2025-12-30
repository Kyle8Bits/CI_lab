package com.kyle.template.todo.external.interfaces;

import com.kyle.template.todo.external.dto.repsonse.UserCreateResponse;
import com.kyle.template.todo.external.dto.request.UserCreateRequest;

public interface UserApi {
    UserCreateResponse createProfile(UserCreateRequest request);
}
