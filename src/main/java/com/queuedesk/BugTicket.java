package com.queuedesk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Set;

public class BugTicket extends Ticket implements Escalatable {

    private final Severity severity;
    private final String stepsToReproduce;
    private boolean escalated;
    private int escalationLevel;

    public BugTicket(String title, String description, String requester, Priority priority,
                     Severity severity, String stepsToReproduce) {
        super(title, description, requester, priority);
        this.severity = severity;
        this.stepsToReproduce = stepsToReproduce;
        this.escalated = false;
        this.escalationLevel = 0;
    }

    @JsonCreator
    public BugTicket(@JsonProperty("id") int id,
                     @JsonProperty("title") String title,
                     @JsonProperty("description") String description,
                     @JsonProperty("requester") String requester,
                     @JsonProperty("priority") Priority priority,
                     @JsonProperty("status") Status status,
                     @JsonProperty("createdAt") LocalDateTime createdAt,
                     @JsonProperty("assignee") String assignee,
                     @JsonProperty("tags") Set<String> tags,
                     @JsonProperty("severity") Severity severity,
                     @JsonProperty("stepsToReproduce") String stepsToReproduce,
                     @JsonProperty("escalated") boolean escalated,
                     @JsonProperty("escalationLevel") int escalationLevel) {
        super(id, title, description, requester, priority, status, createdAt, assignee, tags);
        this.severity = severity;
        this.stepsToReproduce = stepsToReproduce;
        this.escalated = escalated;
        this.escalationLevel = escalationLevel;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getStepsToReproduce() {
        return stepsToReproduce;
    }

    @Override
    public double estimateEffortHours() {
        return switch (severity) {
            case MINOR -> 1.0;
            case MAJOR -> 4.0;
            case CRITICAL -> 8.0;
            case BLOCKER -> 16.0;
        };
    }

    @Override
    public void escalate() {
        this.escalated = true;
        this.escalationLevel++;
    }

    @Override
    public boolean isEscalated() {
        return escalated;
    }

    @Override
    @JsonProperty("escalationLevel")
    public int escalationLevel() {
        return escalationLevel;
    }
}