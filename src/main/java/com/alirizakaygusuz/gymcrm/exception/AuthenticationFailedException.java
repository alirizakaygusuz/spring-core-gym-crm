package com.alirizakaygusuz.gymcrm.exception;

public class AuthenticationFailedException extends GymCrmException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}