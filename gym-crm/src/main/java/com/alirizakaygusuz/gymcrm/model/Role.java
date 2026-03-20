package com.alirizakaygusuz.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private RoleType name;

    @Column(length = 255)
    private String description;

}
