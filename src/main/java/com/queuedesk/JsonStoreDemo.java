package com.queuedesk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonStoreDemo {
    public static void main(String[] args) {
        Path legacyFile = Path.of("tickets.txt");
        Path jsonFile = Path.of("tickets.json");
        Path badFile = Path.of("bad_tickets.json");

        try {
            System.out.println("tickets.json exists before run: " + Files.exists(jsonFile));

            List<Ticket> tickets = TicketMigration.loadOrMigrate(jsonFile, legacyFile);
            System.out.println("Loaded " + tickets.size() + " tickets");
            for (Ticket ticket : tickets) {
                System.out.println("  " + ticket.getClass().getSimpleName() + " -> " + ticket);
            }

            Ticket first = tickets.get(0);
            first.setAssignee("Dave");
            first.setStatus(Status.IN_PROGRESS);
            TicketJsonStore.save(tickets, jsonFile);

            Ticket reloadedFirst = TicketJsonStore.load(jsonFile).get(0);
            System.out.println("After save and reload: " + reloadedFirst
                    + " assigned to " + reloadedFirst.getAssignee());

            Files.writeString(badFile, "[ { \"type\" : \"bug\", \"priority\" : \"URGENT\" } ]");
            try {
                TicketJsonStore.load(badFile);
            } catch (InvalidTicketDataException e) {
                System.out.println("Bad content -> " + e.getMessage());
            }
            Files.deleteIfExists(badFile);

            try {
                TicketJsonStore.load(Path.of("missing.json"));
            } catch (InvalidTicketDataException e) {
                System.out.println("Missing file -> " + e.getMessage());
            }
        } catch (InvalidTicketDataException e) {
            System.out.println("Failed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}