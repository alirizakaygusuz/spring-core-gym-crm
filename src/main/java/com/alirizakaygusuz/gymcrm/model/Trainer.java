package com.alirizakaygusuz.gymcrm.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Trainer extends  User {

    private TrainingType specialization;
   
}
