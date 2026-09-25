package com.queuedesk;

import java.util.List;
import java.util.PriorityQueue;

public class TicketTestRunner {
    public static void main(String[] args) {
        BugTicket bug = new BugTicket("Login crashes", "NPE on login", "Alice", Priority.CRITICAL,
                Severity.BLOCKER, "1. Open app 2. Click login 3. Crash");
        bug.escalate();
        bug.setAssignee("Dave");

        FeatureRequestTicket feature = new FeatureRequestTicket("Add dark mode", "Users want dark UI",
                "Bob", Priority.LOW, 42, "Improves retention");
        feature.setAssignee("Eve");

        AccessRequestTicket access = new AccessRequestTicket("Need admin access", "For deployment",
                "Charlie", Priority.MEDIUM, "AWS Console", "Admin");

        Repository<Ticket> repository = new Repository<>();
        repository.add(bug);
        repository.add(feature);
        repository.add(access);

        TriageBoard.printTriageBoard(repository.all());

        System.out.println("\n=== Grouped by assignee ===");
        repository.groupByAssignee().forEach((assignee, ticketList) ->
                System.out.println(assignee + ": " + ticketList.size() + " ticket(s)"));

        System.out.println("\n=== Natural-order Priority Queue (by priority desc) ===");
        PriorityQueue<Ticket> naturalQueue = new PriorityQueue<>(repository.all());
        while (!naturalQueue.isEmpty()) {
            System.out.println(naturalQueue.poll());
        }

        System.out.println("\n=== Comparator-based Priority Queue (by assignee) ===");
        PriorityQueue<Ticket> assigneeQueue = new PriorityQueue<>(TicketComparators.BY_ASSIGNEE_THEN_PRIORITY);
        assigneeQueue.addAll(repository.all());
        while (!assigneeQueue.isEmpty()) {
            System.out.println(assigneeQueue.poll());
        }

        System.out.println("\nOldest ticket: " + TriageBoard.oldest(repository.all()));

        try {
            repository.findById(999);
        } catch (TicketNotFoundException e) {
            System.out.println("\nCaught expected exception: " + e.getMessage());
        }
    }
}