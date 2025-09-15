

import Services.BalanceService;
import Services.CheckInService;
import Services.SummaryService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("EXIT")) break;

            String[] parts = input.split(" ");
            String command = parts[0];

            switch (command) {
                case "BALANCE" -> {
                    String card = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    BalanceService.addBalance(card, balance);
                }
                case "CHECK_IN" -> {
                    String card = parts[1];
                    String passengerType = parts[2];
                    String station = parts[3];
                    CheckInService.checkIn(card, passengerType, station);
                }
                case "PRINT_SUMMARY" -> SummaryService.printSummary();
                default -> System.out.println("Unknown command: " + command);
            }
        }
        sc.close();
    }
}