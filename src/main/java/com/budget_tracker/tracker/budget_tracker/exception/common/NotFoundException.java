package com.budget_tracker.tracker.budget_tracker.exception.common;

public class NotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Resource not found";

    public NotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public NotFoundException(String message) {
        super(message);
    }

}
