package com.queuedesk;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketAnalyticsTest {

    private BugTicket bug(Priority priority, Severity severity) {
        return new BugTicket("Bug", "desc", "Alice", priority, severity, "steps");
    }

    private Ticket mockTicket(Status status, LocalDateTime createdAt) {
        Ticket ticket = mock(Ticket.class);
        when(ticket.getStatus()).thenReturn(status);
        when(ticket.getCreatedAt()).thenReturn(createdAt);
        return ticket;
    }

    @Test
    void countByPriorityCountsEachPriorityGroup() {
        List<Ticket> tickets = List.of(
                bug(Priority.CRITICAL, Severity.MAJOR),
                bug(Priority.CRITICAL, Severity.MINOR),
                bug(Priority.LOW, Severity.MINOR));

        Map<Priority, Long> counts = TicketAnalytics.countByPriority(tickets);

        assertThat(counts).hasSize(2);
        assertThat(counts).containsEntry(Priority.CRITICAL, 2L);
        assertThat(counts).containsEntry(Priority.LOW, 1L);
    }

    @Test
    void groupByAssigneePutsUnassignedTicketsUnderUnassigned() {
        BugTicket assigned = bug(Priority.HIGH, Severity.MAJOR);
        assigned.setAssignee("Dave");
        BugTicket unassigned = bug(Priority.LOW, Severity.MINOR);

        Map<String, List<Ticket>> grouped = TicketAnalytics.groupByAssignee(List.of(assigned, unassigned));

        assertThat(grouped.keySet()).containsExactlyInAnyOrder("Dave", "Unassigned");
        assertThat(grouped.get("Unassigned")).containsExactly(unassigned);
    }

    @Test
    void averageEffortHoursIsZeroForEmptyList() {
        assertThat(TicketAnalytics.averageEffortHours(List.of())).isEqualTo(0.0);
    }

    @Test
    void averageEffortHoursUsesEstimateOfEachTicket() {
        Ticket quick = mock(Ticket.class);
        Ticket slow = mock(Ticket.class);
        when(quick.estimateEffortHours()).thenReturn(2.0);
        when(slow.estimateEffortHours()).thenReturn(4.0);

        double average = TicketAnalytics.averageEffortHours(List.of(quick, slow));

        assertThat(average).isEqualTo(3.0);
        verify(quick).estimateEffortHours();
        verify(slow).estimateEffortHours();
    }

    @Test
    void totalEffortHoursStreamAndLoopGiveSameResult() {
        List<Ticket> tickets = List.of(
                bug(Priority.HIGH, Severity.BLOCKER),
                bug(Priority.LOW, Severity.MAJOR));

        assertThat(TicketAnalytics.totalEffortHours(tickets)).isEqualTo(20.0);
        assertThat(TicketAnalytics.totalEffortHoursLoop(tickets)).isEqualTo(20.0);
    }

    @Test
    void oldestOpenTicketIsEmptyForEmptyList() {
        assertThat(TicketAnalytics.oldestOpenTicket(List.of())).isEmpty();
    }

    @Test
    void oldestOpenTicketSkipsNonOpenTicketsAndPicksEarliestCreatedAt() {
        Ticket resolvedButOldest = mockTicket(Status.RESOLVED, LocalDateTime.of(2026, 1, 1, 9, 0));
        Ticket newerOpen = mockTicket(Status.OPEN, LocalDateTime.of(2026, 3, 1, 9, 0));
        Ticket olderOpen = mockTicket(Status.OPEN, LocalDateTime.of(2026, 2, 1, 9, 0));

        Optional<Ticket> result = TicketAnalytics.oldestOpenTicket(
                List.of(resolvedButOldest, newerOpen, olderOpen));

        assertThat(result).containsSame(olderOpen);
    }

    @Test
    void tagLineReturnsDistinctSortedTagsJoinedByComma() {
        BugTicket first = bug(Priority.HIGH, Severity.MAJOR);
        first.addTag("urgent");
        first.addTag("auth");
        BugTicket second = bug(Priority.LOW, Severity.MINOR);
        second.addTag("urgent");
        second.addTag("ui");

        assertThat(TicketAnalytics.tagLine(List.of(first, second))).isEqualTo("auth, ui, urgent");
    }
}