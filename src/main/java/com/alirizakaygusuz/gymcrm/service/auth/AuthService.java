package com.alirizakaygusuz.gymcrm.service.auth;

import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;

public interface AuthService {

    void login(String username, String password);

    void changePassword(String currentUsername, ChangePasswordRequest request);

    boolean authenticate(String username, String password);
}
