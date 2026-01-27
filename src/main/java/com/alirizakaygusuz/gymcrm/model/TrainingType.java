package com.alirizakaygusuz.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_types")
@Getter
@Sette
@NoArgsConstructor
@AllArgsConstructor
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_type_name", nullable = false, unique = true, length = 100)
    private TrainingTypeCode trainingTypeName;
}