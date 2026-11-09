package com.queuedesk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositoryTest {

    private Repository<Ticket> repository;
    private BugTicket stored;

    @BeforeEach
    void setUp() {
        repository = new Repository<>();
        stored = new BugTicket("Login crashes", "NPE", "Alice", Priority.HIGH, Severity.MAJOR, "steps");
        repository.add(stored);
    }

    @Test
    void findByIdSafeReturnsTicketWhenPresent() {
        assertThat(repository.findByIdSafe(stored.getId())).containsSame(stored);
    }

    @Test
    void findByIdSafeReturnsEmptyWhenMissing() {
        assertThat(repository.findByIdSafe(-1)).isEmpty();
    }

    @Test
    void findByIdThrowsTicketNotFoundWithIdInMessage() {
        assertThatThrownBy(() -> repository.findById(-1))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("-1");
    }
}