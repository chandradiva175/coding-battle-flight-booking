package com.mitrais.flight.booking.screen;

import com.mitrais.flight.booking.service.AdminService;

import java.util.Scanner;

public class PassengerMenu {

    private final Scanner scanner;
    private final AdminService adminService;

    public PassengerMenu(Scanner scanner, AdminService adminService) {
        this.scanner = scanner;
        this.adminService = adminService;
    }

    public void showPassengerPanel() {
        scanner.nextLine();
        System.out.println("==== PASSENGER LOGIN ====");
        System.out.print("Enter passenger name: ");
        String passengerName = scanner.nextLine();
        System.out.println("Welcome, " + passengerName + "!\n");

        System.out.printf("==== PASSENGER PANEL (%s) ====\n", passengerName);
        System.out.printf("Current day: %d\n", adminService.getDay());
        System.out.println("1. Book a Flight");
        System.out.println("2. Cancel a Booking");
        System.out.println("3. Exit");
    }

}
