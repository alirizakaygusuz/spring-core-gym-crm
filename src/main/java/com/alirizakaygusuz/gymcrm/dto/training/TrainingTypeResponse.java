package com.alirizakaygusuz.gymcrm.dto.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

public record TrainingTypeResponse(
        Long id ,
        TrainingTypeCode trainingType
) {
}
