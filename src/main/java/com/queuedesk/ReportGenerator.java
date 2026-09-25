package com.queuedesk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportGenerator {

    public static String generateReport(List<Ticket> tickets) {
        StringBuilder report = new StringBuilder();
        report.append("=== End of Day Summary ===\n\n");

        Map<Status, Integer> statusCounts = new TreeMap<>();
        Map<Priority, Integer> priorityCounts = new TreeMap<>();

        for (Ticket ticket : tickets) {
            statusCounts.merge(ticket.getStatus(), 1, Integer::sum);
            priorityCounts.merge(ticket.getPriority(), 1, Integer::sum);
        }

        report.append("-- Counts by status --\n");
        for (Map.Entry<Status, Integer> entry : statusCounts.entrySet()) {
            report.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        report.append("\n-- Counts by priority --\n");
        for (Map.Entry<Priority, Integer> entry : priorityCounts.entrySet()) {
            report.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        Ticket oldestOpen = null;
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == Status.OPEN) {
                if (oldestOpen == null || ticket.getCreatedAt().isBefore(oldestOpen.getCreatedAt())) {
                    oldestOpen = ticket;
                }
            }
        }
        report.append("\n-- Oldest open ticket --\n");
        report.append(oldestOpen != null ? oldestOpen.toString() : "None").append("\n");

        report.append("\n-- Overdue CRITICAL tickets (age > 24h) --\n");
        boolean anyOverdue = false;
        for (Ticket ticket : tickets) {
            if (ticket.getPriority() == Priority.CRITICAL
                    && ticket.getStatus() != Status.CLOSED
                    && ticket.ageInHours() > 24) {
                report.append(ticket).append(" - ").append(ticket.ageInHours()).append("h old\n");
                anyOverdue = true;
            }
        }
        if (!anyOverdue) {
            report.append("None\n");
        }

        return report.toString();
    }

    public static void writeReport(String report, Path outputFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            writer.write(report);
        }
    }
}