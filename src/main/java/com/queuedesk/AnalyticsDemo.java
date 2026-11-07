package com.queuedesk;

import java.util.List;

public class AnalyticsDemo {
    public static void main(String[] args) {
        BugTicket bug1 = new BugTicket("Login crashes", "NPE on login", "Alice",
                Priority.CRITICAL, Severity.BLOCKER, "Open app, click login");
        bug1.setAssignee("Dave");
        bug1.addTag("auth");
        bug1.addTag("urgent");

        BugTicket bug2 = new BugTicket("Slow search", "Search takes 10s", "Frank",
                Priority.CRITICAL, Severity.MAJOR, "Search any term");
        bug2.setAssignee("Dave");
        bug2.addTag("performance");

        FeatureRequestTicket feature = new FeatureRequestTicket("Add dark mode", "Users want dark UI",
                "Bob", Priority.LOW, 40, "Improves retention");
        feature.setAssignee("Eve");
        feature.addTag("ui");

        AccessRequestTicket access = new AccessRequestTicket("Need admin access", "For deployment",
                "Charlie", Priority.MEDIUM, "AWS Console", "Admin");
        access.addTag("aws");
        access.addTag("urgent");

        List<Ticket> tickets = List.of(bug1, bug2, feature, access);

        System.out.println("Count by priority: " + TicketAnalytics.countByPriority(tickets));

        System.out.println("\nGrouped by assignee:");
        TicketAnalytics.groupByAssignee(tickets).forEach((assignee, list) ->
                System.out.println("  " + assignee + " -> " + list.size() + " ticket(s)"));

        System.out.println("\nAverage effort: " + TicketAnalytics.averageEffortHours(tickets));
        System.out.println("Average effort (empty list): " + TicketAnalytics.averageEffortHours(List.of()));
        System.out.println("Total (stream): " + TicketAnalytics.totalEffortHours(tickets));
        System.out.println("Total (loop):   " + TicketAnalytics.totalEffortHoursLoop(tickets));

        System.out.println("\nTags: " + TicketAnalytics.tagLine(tickets));
    }
}