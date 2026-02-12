package com.alirizakaygusuz.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "FieldError",
        description = "Represents a validation error for a specific request field."
)
public record FieldError(

        @Schema(
                description = "Name of the field that caused the validation error.",
                example = "firstName"
        )
        String field,

        @Schema(
                description = "Validation error message associated with the field.",
                example = "First name is required"
        )
        String message
) {
}
