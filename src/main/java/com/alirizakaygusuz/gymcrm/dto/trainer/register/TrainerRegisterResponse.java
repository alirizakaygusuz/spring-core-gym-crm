package com.alirizakaygusuz.gymcrm.dto.trainer.register;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterResponse;

public record TrainerRegisterResponse(
        String username,
        String password
) implements UserRegisterResponse {
}
