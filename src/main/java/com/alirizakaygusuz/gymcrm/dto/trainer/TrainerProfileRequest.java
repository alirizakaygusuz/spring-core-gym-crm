package com.alirizakaygusuz.gymcrm.dto.trainer;

import com.alirizakaygusuz.gymcrm.dto.common.UserProfileData;

public record TrainerProfileRequest(String firstName,
                                    String lastName,
                                    Boolean isActive,
                                    Long specializationId) implements UserProfileData {
}
