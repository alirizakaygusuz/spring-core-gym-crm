package com.alirizakaygusuz.gymcrm.controller.api;


import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginResponse;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/v1")
@Tag(
        name = "Authentication",
        description = "User authentication and password management operations"
)
@SecurityRequirement(name = "customAuth")
public interface AuthApi {

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid login request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    ResponseEntity<ApiStandardResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    );



    @PostMapping("/logout")
    ResponseEntity<ApiStandardResponse<Void>> logout();



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
    @SecurityRequirement(name = "customAuth")
    @PatchMapping("/change-password")
    ResponseEntity<ApiStandardResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request


    );
}
