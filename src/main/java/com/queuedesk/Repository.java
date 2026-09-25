package com.queuedesk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository<T extends Ticket> {

    private final List<T> tickets = new ArrayList<>();

    public void add(T ticket) {
        tickets.add(ticket);
    }

    public List<T> all() {
        return Collections.unmodifiableList(tickets);
    }

    public T findById(int id) {
        for (T ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        throw new TicketNotFoundException("No ticket found with id: " + id);
    }

    public Map<String, List<T>> groupByAssignee() {
        Map<String, List<T>> grouped = new HashMap<>();
        for (T ticket : tickets) {
            String key = ticket.getAssignee() == null ? "Unassigned" : ticket.getAssignee();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(ticket);
        }
        return grouped;
    }
}