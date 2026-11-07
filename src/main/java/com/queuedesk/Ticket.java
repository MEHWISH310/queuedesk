package com.queuedesk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BugTicket.class, name = "bug"),
        @JsonSubTypes.Type(value = FeatureRequestTicket.class, name = "feature"),
        @JsonSubTypes.Type(value = AccessRequestTicket.class, name = "access")
})
public abstract class Ticket implements Comparable<Ticket> {

    private static int nextId = 1;

    private final int id;
    private String title;
    private String description;
    private String requester;
    private Priority priority;
    private Status status;
    private final LocalDateTime createdAt;
    private String assignee;
    private final Set<String> tags = new HashSet<>();

    public Ticket(String title, String description, String requester, Priority priority) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.priority = priority;
        this.status = Status.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Ticket(String title, String requester) {
        this(title, "No description provided", requester, Priority.MEDIUM);
    }

    protected Ticket(int id, String title, String description, String requester, Priority priority,
                     Status status, LocalDateTime createdAt, String assignee, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.assignee = assignee;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        nextId = Math.max(nextId, id + 1);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRequester() {
        return requester;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAssignee() {
        return assignee;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public long ageInHours() {
        return ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
    }

    public abstract double estimateEffortHours();

    public static int totalCreated() {
        return nextId - 1;
    }

    @Override
    public int compareTo(Ticket other) {
        int priorityCompare = other.priority.compareTo(this.priority);
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        return this.createdAt.compareTo(other.createdAt);
    }

    @Override
    public String toString() {
        return "Ticket #" + id + " [" + title + "] - " + priority + " - " + status;
    }
}