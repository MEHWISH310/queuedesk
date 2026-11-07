package com.queuedesk;

import java.util.List;
import java.util.Optional;

public class OptionalDemo {
    public static void main(String[] args) {
        BugTicket bug1 = new BugTicket("Login crashes", "NPE on login", "Alice",
                Priority.CRITICAL, Severity.BLOCKER, "Open app, click login");
        bug1.setStatus(Status.RESOLVED);

        BugTicket bug2 = new BugTicket("Slow search", "Search takes 10s", "Frank",
                Priority.HIGH, Severity.MAJOR, "Search any term");

        FeatureRequestTicket feature = new FeatureRequestTicket("Add dark mode", "Users want dark UI",
                "Bob", Priority.LOW, 40, "Improves retention");

        AccessRequestTicket access = new AccessRequestTicket("Need admin access", "For deployment",
                "Charlie", Priority.MEDIUM, "AWS Console", "Admin");

        List<Ticket> tickets = List.of(bug1, bug2, feature, access);

        Repository<Ticket> repository = new Repository<>();
        tickets.forEach(repository::add);

        int missingId = 99;

        Optional<Ticket> oldestOpen = TicketAnalytics.oldestOpenTicket(tickets);
        System.out.println("Oldest open: " + oldestOpen.map(Ticket::getTitle).orElse("none"));
        System.out.println("Oldest open (empty list): "
                + TicketAnalytics.oldestOpenTicket(List.of()).map(Ticket::getTitle).orElse("none"));

        System.out.println();
        System.out.println("Title of #2: "
                + repository.findByIdSafe(2).map(Ticket::getTitle).orElse("not found"));
        System.out.println("Title of #" + missingId + ": "
                + repository.findByIdSafe(missingId).map(Ticket::getTitle).orElse("not found"));

        System.out.println();
        repository.findByIdSafe(3).ifPresentOrElse(
                t -> System.out.println("Found: " + t),
                () -> System.out.println("Ticket 3 not found"));
        repository.findByIdSafe(missingId).ifPresentOrElse(
                t -> System.out.println("Found: " + t),
                () -> System.out.println("Ticket " + missingId + " not found"));

        System.out.println();
        try {
            Ticket found = repository.findByIdSafe(missingId)
                    .orElseThrow(() -> new TicketNotFoundException(missingId));
            System.out.println("Found: " + found);
        } catch (TicketNotFoundException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        try {
            repository.findById(missingId);
        } catch (TicketNotFoundException e) {
            System.out.println("Caught from findById: " + e.getMessage());
        }
    }
}