package com.queuedesk;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketAnalytics {

    public static Map<Priority, Long> countByPriority(List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(Ticket::getPriority, Collectors.counting()));
    }

    public static Map<String, List<Ticket>> groupByAssignee(List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getAssignee() == null ? "Unassigned" : t.getAssignee()));
    }

    public static double averageEffortHours(List<Ticket> tickets) {
        return tickets.stream()
                .mapToDouble(Ticket::estimateEffortHours)
                .average()
                .orElse(0.0);
    }

    public static double totalEffortHours(List<Ticket> tickets) {
        return tickets.stream()
                .map(Ticket::estimateEffortHours)
                .reduce(0.0, Double::sum, Double::sum);
    }

    public static double totalEffortHoursLoop(List<Ticket> tickets) {
        double total = 0.0;
        for (Ticket ticket : tickets) {
            total += ticket.estimateEffortHours();
        }
        return total;
    }

    public static String tagLine(List<Ticket> tickets) {
        return tickets.stream()
                .flatMap(t -> t.getTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
    }
}