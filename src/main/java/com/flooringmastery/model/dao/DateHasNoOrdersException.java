package com.flooringmastery.model.dao;

public class DateHasNoOrdersException extends Exception {
    public DateHasNoOrdersException(String message) {
        super(message);
    }

    public DateHasNoOrdersException(String message, Throwable cause) {
        super(message, cause);
    }
}