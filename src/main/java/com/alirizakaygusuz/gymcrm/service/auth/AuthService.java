package com.alirizakaygusuz.gymcrm.service.auth;

import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void changePassword(ChangePasswordRequest request);

    boolean authenticate(String username, String password);
}
