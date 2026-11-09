package com.queuedesk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketJsonStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoadRestoresEachTicketAsItsRealSubtype() throws InvalidTicketDataException {
        BugTicket bug = new BugTicket("Login crashes", "NPE", "Alice",
                Priority.CRITICAL, Severity.BLOCKER, "steps");
        bug.setAssignee("Dave");
        bug.addTag("auth");
        bug.escalate();
        bug.escalate();
        FeatureRequestTicket feature = new FeatureRequestTicket("Dark mode", "UI", "Bob",
                Priority.LOW, 40, "retention");
        AccessRequestTicket access = new AccessRequestTicket("Admin access", "Deploy", "Charlie",
                Priority.MEDIUM, "AWS", "Admin");
        Path file = tempDir.resolve("tickets.json");

        TicketJsonStore.save(List.of(bug, feature, access), file);
        List<Ticket> loaded = TicketJsonStore.load(file);

        assertThat(loaded).hasSize(3);
        assertThat(loaded.get(0)).isInstanceOf(BugTicket.class);
        assertThat(loaded.get(1)).isInstanceOf(FeatureRequestTicket.class);
        assertThat(loaded.get(2)).isInstanceOf(AccessRequestTicket.class);

        BugTicket loadedBug = (BugTicket) loaded.get(0);
        assertThat(loadedBug.getId()).isEqualTo(bug.getId());
        assertThat(loadedBug.getAssignee()).isEqualTo("Dave");
        assertThat(loadedBug.getTags()).containsExactly("auth");
        assertThat(loadedBug.escalationLevel()).isEqualTo(2);
        assertThat(loadedBug.getCreatedAt()).isEqualTo(bug.getCreatedAt());
    }

    @Test
    void loadWrapsMalformedContentInInvalidTicketDataException() throws IOException {
        Path file = tempDir.resolve("bad.json");
        Files.writeString(file, "[ { \"type\" : \"bug\", \"priority\" : \"URGENT\" } ]");

        assertThatThrownBy(() -> TicketJsonStore.load(file))
                .isInstanceOf(InvalidTicketDataException.class)
                .hasMessageContaining("Invalid ticket data")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void loadWrapsMissingFileInInvalidTicketDataException() {
        Path missing = tempDir.resolve("missing.json");

        assertThatThrownBy(() -> TicketJsonStore.load(missing))
                .isInstanceOf(InvalidTicketDataException.class)
                .hasMessageContaining("Could not read");
    }
}