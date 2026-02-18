package com.alirizakaygusuz.gymcrm.dto.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TrainingTypeResponse",
        description = "Response object representing an available training type."
)
public record TrainingTypeResponse(

        @Schema(
                description = "Unique identifier of the training type.",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Code representing the training type.",
                example = "CARDIO"
        )
        TrainingTypeCode trainingType
) {
}
