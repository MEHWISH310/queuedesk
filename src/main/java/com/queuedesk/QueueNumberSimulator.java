package com.queuedesk;

import java.util.Scanner;

public class QueueNumberSimulator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter starting ticket number: ");
        int startingTicket = scanner.nextInt();

        System.out.print("Enter number of customers to simulate: ");
        int customerCount = scanner.nextInt();

        int servedCount = 0;
        int vipCount = 0;
        int priorityRecheckCount = 0;

        for (int i = 0; i < customerCount; i++) {
            int currentTicket = startingTicket + i;
            servedCount++;

            StringBuilder message = new StringBuilder("Now serving ticket #" + currentTicket);

            if (currentTicket % 10 == 0) {
                message.append(" - PRIORITY RECHECK");
                priorityRecheckCount++;
            } else if (currentTicket % 5 == 0) {
                message.append(" - VIP LANE");
                vipCount++;
            }

            System.out.println(message);
        }

        System.out.println("\n--- End of Day Summary ---");
        System.out.println("Total served: " + servedCount);
        System.out.println("VIP lane tickets: " + vipCount);
        System.out.println("Priority recheck tickets: " + priorityRecheckCount);

        scanner.close();
    }
}