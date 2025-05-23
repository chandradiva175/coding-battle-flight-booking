package com.mitrais.flight.booking.screen;

import com.mitrais.flight.booking.pojo.Destination;
import com.mitrais.flight.booking.service.AdminService;
import com.mitrais.flight.booking.service.PassengerService;

import java.util.Scanner;
import java.util.stream.Collectors;

public class PassengerMenu {

    private final Scanner scanner;
    private final AdminService adminService;
    private final PassengerService passengerService;

    public PassengerMenu(Scanner scanner, AdminService adminService, PassengerService passengerService) {
        this.scanner = scanner;
        this.adminService = adminService;
        this.passengerService = passengerService;
    }

    public void showPassengerPanel() {
        scanner.nextLine();
        System.out.println("==== PASSENGER LOGIN ====");
        System.out.print("Enter passenger name: ");
        String passengerName = scanner.nextLine();
        System.out.println("Welcome, " + passengerName + "!\n");

        showPassengerPanel(passengerName);
    }

    public void showPassengerPanel(String passengerName) {
        System.out.printf("==== PASSENGER PANEL (%s) ====\n", passengerName);
        System.out.printf("Current day: %d\n", adminService.getDay());
        System.out.println("1. Book a Flight");
        System.out.println("2. Cancel a Booking");
        System.out.println("3. Exit");
        System.out.println();
        System.out.print("> ");

        int option = scanner.nextInt();
        switch (option) {
            case 1:
                showBookFlight(passengerName);
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    public void showBookFlight(String passengerName) {
        String availDestination = adminService.getDestinations() == null ? "" : adminService.getDestinations().stream()
                .map(Destination::getName)
                .collect(Collectors.joining(", "));

        scanner.nextLine();
        System.out.println("== BOOK A FLIGHT ==");
        System.out.println("Available destinations: " + availDestination);

        System.out.print("Enter departure city: ");
        String departureCity = scanner.nextLine();
        Destination from = adminService.getMapDestination().get(departureCity);

        System.out.print("Enter destination city: ");
        String destinationCity = scanner.nextLine();
        Destination to = adminService.getMapDestination().get(destinationCity);

        passengerService.searchFlight(adminService.getFlightRoutes(), from, to);
        showPassengerPanel(passengerName);
    }

}
