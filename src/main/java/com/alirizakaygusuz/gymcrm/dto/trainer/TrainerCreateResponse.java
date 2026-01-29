package com.alirizakaygusuz.gymcrm.dto.trainer;

public record TrainerCreateResponse(Long trainerId,
                                     String username,
                                     boolean active) {
}
