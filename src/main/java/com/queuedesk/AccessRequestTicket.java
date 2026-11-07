package com.queuedesk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Set;

public class AccessRequestTicket extends Ticket implements Escalatable {

    private final String systemName;
    private final String accessLevel;
    private boolean escalated;
    private int escalationLevel;

    public AccessRequestTicket(String title, String description, String requester, Priority priority,
                               String systemName, String accessLevel) {
        super(title, description, requester, priority);
        this.systemName = systemName;
        this.accessLevel = accessLevel;
        this.escalated = false;
        this.escalationLevel = 0;
    }

    @JsonCreator
    public AccessRequestTicket(@JsonProperty("id") int id,
                               @JsonProperty("title") String title,
                               @JsonProperty("description") String description,
                               @JsonProperty("requester") String requester,
                               @JsonProperty("priority") Priority priority,
                               @JsonProperty("status") Status status,
                               @JsonProperty("createdAt") LocalDateTime createdAt,
                               @JsonProperty("assignee") String assignee,
                               @JsonProperty("tags") Set<String> tags,
                               @JsonProperty("systemName") String systemName,
                               @JsonProperty("accessLevel") String accessLevel,
                               @JsonProperty("escalated") boolean escalated,
                               @JsonProperty("escalationLevel") int escalationLevel) {
        super(id, title, description, requester, priority, status, createdAt, assignee, tags);
        this.systemName = systemName;
        this.accessLevel = accessLevel;
        this.escalated = escalated;
        this.escalationLevel = escalationLevel;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    @Override
    public double estimateEffortHours() {
        return 0.5;
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