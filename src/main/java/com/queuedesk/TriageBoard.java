package com.queuedesk;

import java.util.List;

public class TriageBoard {

    public static void printTriageBoard(List<Ticket> tickets) {
        System.out.println("=== Triage Board ===");
        for (Ticket ticket : tickets) {
            System.out.println(ticket + " | Estimated effort: " + ticket.estimateEffortHours() + "h");

            if (ticket instanceof Escalatable e) {
                System.out.println("   " + e.escalationBadge());
            }
        }
    }
}