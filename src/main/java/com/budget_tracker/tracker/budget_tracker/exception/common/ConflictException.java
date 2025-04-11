package com.budget_tracker.tracker.budget_tracker.exception.common;

public class ConflictException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Conflict occurred";

    public ConflictException() {
        super(DEFAULT_MESSAGE);
    }

    public ConflictException(String message) {
        super(message);
    }
}
