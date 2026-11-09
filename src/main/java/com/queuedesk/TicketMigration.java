package com.queuedesk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class TicketMigration {

    private TicketMigration() {
    }

    public static int migrate(Path legacyFile, Path jsonFile) throws InvalidTicketDataException, IOException {
        List<Ticket> tickets = TicketFileLoader.load(legacyFile);
        TicketJsonStore.save(tickets, jsonFile);
        return tickets.size();
    }

    public static List<Ticket> loadOrMigrate(Path jsonFile, Path legacyFile)
            throws InvalidTicketDataException, IOException {
        if (Files.exists(jsonFile)) {
            return TicketJsonStore.load(jsonFile);
        }
        if (Files.exists(legacyFile)) {
            migrate(legacyFile, jsonFile);
            return TicketJsonStore.load(jsonFile);
        }
        return new ArrayList<>();
    }
}