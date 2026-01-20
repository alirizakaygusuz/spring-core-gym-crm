package com.alirizakaygusuz.gymcrm.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;

    @ToString.Exclude
    private String password;
    private boolean active;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

}
