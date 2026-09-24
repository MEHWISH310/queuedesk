package com.queuedesk;

import java.util.ArrayList;
import java.util.List;

public class TicketTestRunner {
    public static void main(String[] args) {
        List<Ticket> tickets = new ArrayList<>();

        BugTicket bug = new BugTicket("Login crashes", "NPE on login", "Alice", Priority.CRITICAL,
                Severity.BLOCKER, "1. Open app 2. Click login 3. Crash");
        bug.escalate();

        FeatureRequestTicket feature = new FeatureRequestTicket("Add dark mode", "Users want dark UI",
                "Bob", Priority.LOW, 42, "Improves retention");

        AccessRequestTicket access = new AccessRequestTicket("Need admin access", "For deployment",
                "Charlie", Priority.MEDIUM, "AWS Console", "Admin");

        tickets.add(bug);
        tickets.add(feature);
        tickets.add(access);

        TriageBoard.printTriageBoard(tickets);
    }
}