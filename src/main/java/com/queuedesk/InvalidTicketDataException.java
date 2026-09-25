package com.queuedesk;

public class InvalidTicketDataException extends Exception {
    public InvalidTicketDataException(String message) {
        super(message);
    }
}