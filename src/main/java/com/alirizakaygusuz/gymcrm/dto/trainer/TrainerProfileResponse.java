package com.alirizakaygusuz.gymcrm.dto.trainer;

public record TrainerProfileResponse (Long trainerId,
                                     String firstName,
                                     String lastName,
                                     boolean active,
                                     String username,
                                     Long specializationId){
}