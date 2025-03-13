package com.budget_tracker.tracker.budget_tracker.exception.user;

public class UserNotFoundException  extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}