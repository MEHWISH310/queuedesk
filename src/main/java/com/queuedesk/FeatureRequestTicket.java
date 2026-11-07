package com.queuedesk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Set;

public class FeatureRequestTicket extends Ticket {

    private final int votes;
    private final String businessJustification;

    public FeatureRequestTicket(String title, String description, String requester, Priority priority,
                                int votes, String businessJustification) {
        super(title, description, requester, priority);
        this.votes = votes;
        this.businessJustification = businessJustification;
    }

    @JsonCreator
    public FeatureRequestTicket(@JsonProperty("id") int id,
                                @JsonProperty("title") String title,
                                @JsonProperty("description") String description,
                                @JsonProperty("requester") String requester,
                                @JsonProperty("priority") Priority priority,
                                @JsonProperty("status") Status status,
                                @JsonProperty("createdAt") LocalDateTime createdAt,
                                @JsonProperty("assignee") String assignee,
                                @JsonProperty("tags") Set<String> tags,
                                @JsonProperty("votes") int votes,
                                @JsonProperty("businessJustification") String businessJustification) {
        super(id, title, description, requester, priority, status, createdAt, assignee, tags);
        this.votes = votes;
        this.businessJustification = businessJustification;
    }

    public int getVotes() {
        return votes;
    }

    public String getBusinessJustification() {
        return businessJustification;
    }

    @Override
    public double estimateEffortHours() {
        return 5.0 + (votes * 0.1);
    }
}