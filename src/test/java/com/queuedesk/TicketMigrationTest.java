package com.queuedesk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TicketMigrationTest {

    @TempDir
    Path tempDir;

    @Test
    void migratesLegacyFileToJsonOnceThenReadsFromJson() throws IOException, InvalidTicketDataException {
        Path legacy = tempDir.resolve("tickets.txt");
        Path json = tempDir.resolve("tickets.json");
        Files.writeString(legacy,
                "Login broken|Cannot log in|Alice|HIGH|OPEN\n"
                        + "Dark mode|Users want it|Bob|LOW|IN_PROGRESS\n");

        List<Ticket> first = TicketMigration.loadOrMigrate(json, legacy);

        assertThat(json).exists();
        assertThat(first).hasSize(2);

        Files.delete(legacy);
        List<Ticket> second = TicketMigration.loadOrMigrate(json, legacy);

        assertThat(second).extracting(Ticket::getTitle).containsExactly("Login broken", "Dark mode");
    }

    @Test
    void returnsEmptyListWhenNeitherFileExists() throws IOException, InvalidTicketDataException {
        List<Ticket> tickets = TicketMigration.loadOrMigrate(
                tempDir.resolve("none.json"), tempDir.resolve("none.txt"));

        assertThat(tickets).isEmpty();
    }
}