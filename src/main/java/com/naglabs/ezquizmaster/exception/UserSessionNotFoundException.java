package com.naglabs.ezquizmaster.exception;

public class UserSessionNotFoundException extends RuntimeException {
    public UserSessionNotFoundException(String message) {
        super(message);
    }
}

