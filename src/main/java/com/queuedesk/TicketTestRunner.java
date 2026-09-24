package com.queuedesk;

public class TicketTestRunner {
    public static void main(String[] args) {
        Ticket t1 = new Ticket("Login page broken", "Can't log in on Chrome", "Alice", Priority.HIGH);
        Ticket t2 = new Ticket("Add dark mode", "Bob");

        System.out.println(t1);
        System.out.println(t2);
        System.out.println("Age of t1 (hours): " + t1.ageInHours());
        System.out.println("Total tickets created: " + Ticket.totalCreated());
    }
}