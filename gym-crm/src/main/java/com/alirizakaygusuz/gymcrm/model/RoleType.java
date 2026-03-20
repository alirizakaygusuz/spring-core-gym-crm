package com.alirizakaygusuz.gymcrm.model;

public enum RoleType {
    TRAINER,
    TRAINEE;


    public String asAuthority() {
        return "ROLE_" + this.name();
    }

    public String getRoleName() {
        return this.name();
    }
}
