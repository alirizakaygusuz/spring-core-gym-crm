package com.alirizakaygusuz.gymcrm.model;

import java.time.LocalDate;

public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;


    public Trainee() {
    }

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, LocalDate dateOfBirth, String address) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Trainee(Long id, String firstName, String lastName, String username, String password, boolean isActive, LocalDate dateOfBirth, String address) {
        super(id, firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "Trainee{" +
                super.toString() +", " +
                "dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}
