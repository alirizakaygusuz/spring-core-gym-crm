package com.alirizakaygusuz.gymcrm.dto.trainee;

public record TraineeCreateResponse(Long traineeId,
                                    String username,
                                    Boolean isActive
                                    ) {
}
