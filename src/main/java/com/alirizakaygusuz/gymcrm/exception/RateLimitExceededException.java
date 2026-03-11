package com.alirizakaygusuz.gymcrm.exception;

public class RateLimitExceededException extends GymCrmException{


    protected RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String username, long remainingSeconds) {
        super(String.format(
                "Too many login attempts for user '%s'. Account temporarily locked. Try again in %d seconds.",
                username,
                remainingSeconds
        ));
    }


}
