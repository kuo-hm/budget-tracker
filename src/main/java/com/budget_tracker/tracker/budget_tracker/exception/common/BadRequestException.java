package com.budget_tracker.tracker.budget_tracker.exception.common;

public class BadRequestException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Bad request";

    public BadRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public BadRequestException(String message) {
        super(message);
    }

}
