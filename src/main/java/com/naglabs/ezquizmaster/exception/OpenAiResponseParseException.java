package com.naglabs.ezquizmaster.exception;

public class OpenAiResponseParseException extends RuntimeException {
    public OpenAiResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

