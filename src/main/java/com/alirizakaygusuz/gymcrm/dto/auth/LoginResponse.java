package com.alirizakaygusuz.gymcrm.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing JWT token and its metadata")
public record LoginResponse(
        @Schema(
                description = "JWT access token issued upon successful authentication",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTY4ODQyODAwMCwiZXhwIjoxNjg4NDMyNjAwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        )
        String accessToken,

        @Schema(
                description = "Type of the token, typically 'Bearer'",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Duration in seconds for which the token is valid",
                example = "3600"
        )
        long expiresIn
) {
}
