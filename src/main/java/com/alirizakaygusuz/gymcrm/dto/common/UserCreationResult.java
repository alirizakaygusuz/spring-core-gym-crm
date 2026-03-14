package com.alirizakaygusuz.gymcrm.dto.common;

import com.alirizakaygusuz.gymcrm.model.User;

public record UserCreationResult(
        User user,
        String rawPassword
) {
}
