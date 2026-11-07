package com.queuedesk;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(int id) {
        super("No ticket found with id: " + id);
    }
}