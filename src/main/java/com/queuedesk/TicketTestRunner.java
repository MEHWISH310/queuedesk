package com.queuedesk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TicketTestRunner {
    public static void main(String[] args) {
        Path inputFile = Path.of("tickets.txt");
        Path outputFile = Path.of("daily_report.txt");

        try {
            List<Ticket> tickets = TicketFileLoader.load(inputFile);
            System.out.println("Loaded " + tickets.size() + " tickets from " + inputFile);

            String report = ReportGenerator.generateReport(tickets);
            System.out.println("\n" + report);

            ReportGenerator.writeReport(report, outputFile);
            System.out.println("Report written to " + outputFile);

            Repository<Ticket> repository = new Repository<>();
            for (Ticket t : tickets) {
                repository.add(t);
            }
            repository.findById(999); // deliberately trigger the unchecked exception

        } catch (InvalidTicketDataException e) {
            System.out.println("Failed to load tickets: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        } catch (TicketNotFoundException e) {
            System.out.println("Ticket lookup failed: " + e.getMessage());
        }
    }
}