package com.alirizakaygusuz.gymcrm.workload_service.exception;

public class JwtTokenException extends WorkloadServiceException{
    public JwtTokenException(String message) {
        super(message);
    }

    public JwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
