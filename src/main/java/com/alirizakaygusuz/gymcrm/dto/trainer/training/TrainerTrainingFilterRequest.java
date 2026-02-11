package com.alirizakaygusuz.gymcrm.dto.trainer.training;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TrainerTrainingFilterRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate periodFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate periodTo,

        String traineeName

) {
}
