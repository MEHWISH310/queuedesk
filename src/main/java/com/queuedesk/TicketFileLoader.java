package com.queuedesk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TicketFileLoader {

    public static List<Ticket> load(Path file) throws InvalidTicketDataException, IOException {
        List<Ticket> tickets = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length != 5) {
                    throw new InvalidTicketDataException(
                            "Line " + lineNumber + ": expected 5 fields, found " + fields.length);
                }

                String title = fields[0].trim();
                String description = fields[1].trim();
                String requester = fields[2].trim();

                Priority priority;
                try {
                    priority = Priority.valueOf(fields[3].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidTicketDataException(
                            "Line " + lineNumber + ": invalid priority '" + fields[3].trim() + "'");
                }

                Status status;
                try {
                    status = Status.valueOf(fields[4].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidTicketDataException(
                            "Line " + lineNumber + ": invalid status '" + fields[4].trim() + "'");
                }

                FeatureRequestTicket ticket = new FeatureRequestTicket(
                        title, description, requester, priority, 0, "Loaded from file");
                ticket.setStatus(status);
                tickets.add(ticket);
            }
        }

        return tickets;
    }
}