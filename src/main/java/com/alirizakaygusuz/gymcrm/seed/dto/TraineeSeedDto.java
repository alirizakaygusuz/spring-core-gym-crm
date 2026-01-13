package com.alirizakaygusuz.gymcrm.seed.dto;

import java.time.LocalDate;

public record TraineeSeedDto(
        long id,
        String firstName,
        String lastName,
        boolean isActive,
        LocalDate dateOfBirth,
        String address
) {
}
