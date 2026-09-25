package com.queuedesk;

import java.util.Comparator;

public class TicketComparators {

    public static final Comparator<Ticket> BY_ASSIGNEE_THEN_PRIORITY =
            Comparator.comparing((Ticket t) -> t.getAssignee() == null ? "" : t.getAssignee())
                    .thenComparing(Ticket::getPriority);

    public static final Comparator<Ticket> BY_STATUS_THEN_AGE =
            Comparator.comparing(Ticket::getStatus)
                    .thenComparing(Ticket::ageInHours);
}