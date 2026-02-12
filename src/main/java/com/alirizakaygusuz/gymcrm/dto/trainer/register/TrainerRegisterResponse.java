package com.alirizakaygusuz.gymcrm.dto.trainer.register;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TrainerRegisterResponse",
        description = "Response object containing the generated credentials for a newly registered trainer."
)
public record TrainerRegisterResponse(

        @Schema(
                description = "Generated username for the trainer.",
                example = "trainer_jane"
        )
        String username,

        @Schema(
                description = "Generated initial password for the trainer.",
                example = "P@ssw0rd123"
        )
        String password
) implements UserRegisterResponse {
}
