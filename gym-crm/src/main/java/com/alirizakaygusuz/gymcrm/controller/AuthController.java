package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.controller.api.AuthApi;
import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginResponse;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController implements AuthApi {


    private final AuthService authService;

    @Override
    public ResponseEntity<ApiStandardResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ok(authService.login(loginRequest));
    }

    @Override
    public ResponseEntity<ApiStandardResponse<Void>> logout() {
        return ok();
    }

    @Override
    @PreAuthorize("authentication.name == #request.username")
    public ResponseEntity<ApiStandardResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ok();
    }
}