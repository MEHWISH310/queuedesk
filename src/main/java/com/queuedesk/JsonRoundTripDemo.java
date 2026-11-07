package com.queuedesk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonRoundTripDemo {
    public static void main(String[] args) throws JsonProcessingException {
        BugTicket bug = new BugTicket("Login crashes", "NPE on login", "Alice",
                Priority.CRITICAL, Severity.BLOCKER, "Open app, click login");
        bug.setAssignee("Dave");
        bug.addTag("auth");
        bug.escalate();
        bug.escalate();

        FeatureRequestTicket feature = new FeatureRequestTicket("Add dark mode", "Users want dark UI",
                "Bob", Priority.LOW, 40, "Improves retention");

        AccessRequestTicket access = new AccessRequestTicket("Need admin access", "For deployment",
                "Charlie", Priority.MEDIUM, "AWS Console", "Admin");
        access.escalate();

        List<Ticket> original = List.of(bug, feature, access);

        ObjectMapper mapper = JsonMapperProvider.get();
        String json = mapper.writerFor(new TypeReference<List<Ticket>>() { }).writeValueAsString(original);
        System.out.println(json);

        List<Ticket> loaded = mapper.readValue(json, new TypeReference<List<Ticket>>() { });

        System.out.println();
        for (Ticket ticket : loaded) {
            System.out.println(ticket.getClass().getSimpleName() + " -> " + ticket);
        }

        BugTicket loadedBug = (BugTicket) loaded.get(0);
        System.out.println();
        System.out.println("Escalation level restored: " + loadedBug.escalationLevel());
        System.out.println("createdAt preserved: " + loadedBug.getCreatedAt().equals(bug.getCreatedAt()));
        System.out.println("Tags restored: " + loadedBug.getTags());
    }
}