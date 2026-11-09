package com.queuedesk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class TicketJsonStore {

    private static final TypeReference<List<Ticket>> TICKET_LIST = new TypeReference<List<Ticket>>() { };

    private TicketJsonStore() {
    }

    public static void save(List<Ticket> tickets, Path file) throws InvalidTicketDataException {
        try {
            JsonMapperProvider.get().writerFor(TICKET_LIST).writeValue(file.toFile(), tickets);
        } catch (IOException e) {
            throw new InvalidTicketDataException("Could not write tickets to " + file, e);
        }
    }

    public static List<Ticket> load(Path file) throws InvalidTicketDataException {
        try {
            return JsonMapperProvider.get().readValue(file.toFile(), TICKET_LIST);
        } catch (JsonProcessingException e) {
            throw new InvalidTicketDataException("Invalid ticket data in " + file, e);
        } catch (IOException e) {
            throw new InvalidTicketDataException("Could not read " + file, e);
        }
    }
}