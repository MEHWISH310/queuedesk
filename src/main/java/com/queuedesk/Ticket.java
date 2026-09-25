package com.queuedesk;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private java.util.Set<String> tags = new java.util.HashSet<>();

    // Full constructor
    public Ticket(String title, String description, String requester, Priority priority) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.priority = priority;
        this.status = Status.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    // Short constructor - chains to full constructor with sensible defaults
    public Ticket(String title, String requester) {
        this(title, "No description provided", requester, Priority.MEDIUM);
    }

    // Getters
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

    // Setters - only for description, priority, status
    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

        public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public java.util.Set<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    // Instance method
    public long ageInHours() {
        return ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
    }

    public abstract double estimateEffortHours();

    // Static method
    public static int totalCreated() {
        return nextId - 1;
    }

    @Override
    public String toString() {
        return "Ticket #" + id + " [" + title + "] - " + priority + " - " + status;
    }

    @Override
    public int compareTo(Ticket other) {
        int priorityCompare = other.priority.compareTo(this.priority); // descending
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        return this.createdAt.compareTo(other.createdAt); // ascending for ties
    }
}