package com.alirizakaygusuz.gymcrm.dto.trainee.register;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TraineeRegisterResponse",
        description = "Response object containing the username and password of a newly registered trainee"
)
public record TraineeRegisterResponse(

        @Schema(
                description = "Generated username",
                example = "john_doe"
        )
        String username,

        @Schema(
                description = "Generated password",
                example = "P@ssw0rd123"
        )
        String password

) implements UserRegisterResponse {
}
