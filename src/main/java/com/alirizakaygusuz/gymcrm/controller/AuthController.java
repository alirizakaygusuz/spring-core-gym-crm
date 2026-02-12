package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.filter.CurrentUserExtractor;
import com.alirizakaygusuz.gymcrm.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "Login user",
            description = "Authenticates user with username and password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @GetMapping("/login")
    public ResponseEntity<ApiStandardResponse<Void>> login(
            @Parameter(description = "Username", example = "john.doe", required = true)
            @RequestParam("username") String username,

            @Parameter(description = "Password", example = "P@ssw0rd", required = true)
            @RequestParam("password") String password
    ) {
        authService.login(username, password);
        return ok();
    }

    @Operation(
            summary = "Change user password",
            description = "Allows authenticated users to change their password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PatchMapping("/change-password")
    public ResponseEntity<ApiStandardResponse<Void>> changePassword(
            @RequestBody(required = false)
            HttpServletRequest httpServletRequest,

            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        authService.changePassword(currentUsername, request);
        return ok();
    }
}
