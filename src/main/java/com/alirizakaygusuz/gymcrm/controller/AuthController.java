package com.alirizakaygusuz.gymcrm.controller;


import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.response.ApiResponse;
import com.alirizakaygusuz.gymcrm.filter.CurrentUserExtractor;
import com.alirizakaygusuz.gymcrm.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;
    private final CurrentUserExtractor currentUserExtractor;

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestParam String username, @RequestParam String password) {
        authService.login(username , password);
        return ok();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);

        authService.changePassword(currentUsername, request);
        return ok();
    }


}
