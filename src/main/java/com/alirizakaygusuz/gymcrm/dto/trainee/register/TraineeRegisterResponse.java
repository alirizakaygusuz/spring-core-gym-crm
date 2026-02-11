package com.alirizakaygusuz.gymcrm.dto.trainee.register;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterResponse;

public record TraineeRegisterResponse(
        String username,
        String password
) implements UserRegisterResponse {
}
