package com.queuedesk;

public interface Escalatable {
    void escalate();
    boolean isEscalated();
    int escalationLevel();

    default String escalationBadge() {
        return isEscalated() ? "[ESCALATED - Level " + escalationLevel() + "]" : "[Not escalated]";
    }
}