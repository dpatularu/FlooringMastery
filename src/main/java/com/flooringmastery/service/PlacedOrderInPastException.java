package com.flooringmastery.service;

public class PlacedOrderInPastException extends Exception{
    public PlacedOrderInPastException(String message) {
        super(message);
    }

    public PlacedOrderInPastException(String message, Throwable cause) {
        super(message, cause);
    }
}