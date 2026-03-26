package com.alirizakaygusuz.gymcrm.exception;

public class ResourceNotFoundException extends GymCrmException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
