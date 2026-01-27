package com.alirizakaygusuz.gymcrm.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;

}
