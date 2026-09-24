package com.queuedesk;

public class BugTicket extends Ticket implements Escalatable {

    private Severity severity;
    private String stepsToReproduce;
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
    public int escalationLevel() {
        return escalationLevel;
    }
}