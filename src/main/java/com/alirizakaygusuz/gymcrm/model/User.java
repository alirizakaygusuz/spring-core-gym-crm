package com.alirizakaygusuz.gymcrm.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public abstract class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;

    @ToString.Exclude
    private String password;
    private boolean active;

}
